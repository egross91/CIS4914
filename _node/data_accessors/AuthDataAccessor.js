'use strict';

/**
 * Modules.
 **/
var Postgres       = require('pg');
var PasswordHasher = require('password-hash');
var JWT            = require('jsonwebtoken');
var ErrorHelper    = require('../helpers/ErrorHelper');

/**
 * Static strings.
 **/
var postgresConnectionString = process.env.MU_CONN_STR;
var jwtSecret                = process.env.MU_JWT_SECRET;

/** 
 * Helper Objects.
 **/
var jwtOptions = { expiresIn: 7200 }; // In Seconds.

/**
 * @param data: User registration information.
 * @param send: Callback.
 * @return: JSON Webtoken with user session information.
 **/
exports.register = function (data, send) {
  var email        = data.email;
  var rawPassword  = data.password;
  var errorHandler = { hasErrors: false, messages: [], statusCode: 200 };

  /* Connect to database. */
  Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connecting to database. **/
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Failed to connect to database.");

      send(errorHandler);
      return;
    }

    var queryPreparedStatement = "SELECT email " +
                                 "FROM User_Pers " +
                                 "WHERE email = $1;";
    var queryInserts           = [ email ];
    var userData               = {};

    /** Check if email exists. **/
    client.query(queryPreparedStatement, queryInserts, function (err, result) {
      if (err) {
        /*** Problem while querying for email. ***/
        ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.

        send(errorHandler, userData);
        done(); // Close DB connection.
        return;
      }

      if (result.rows.length > 0) {
        /*** User exists. ***/
        ErrorHelper.addMessages(errorHandler, 409, (email + " already exists! :'(")); // Conflict.

        send(errorHandler, userData);
        done(); // Close DB connection.
      } else if (result.rows.length === 0) {
        /*** User does NOT exist. ***/
        var setupPreparedStatement = "SELECT * " +
                                     "FROM setup_user($1, $2) AS f(success);";
        var saltedPassword         = PasswordHasher.generate(rawPassword);
        var insertInserts          = [ email, saltedPassword ];

        /*** Create new user in database. **/
        client.query(setupPreparedStatement, insertInserts, function (err, result) {
          /**** Problem inserting into database. ****/
          if (err) {
            ErrorHelper.mergeMessages(errorHandler, 500, err);
            ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Error querying.");
          } 

          if (result.rows[0].success) {
            var token = JWT.sign(userData, jwtSecret, jwtOptions);
            send(errorHandler, token);
          } else {
            ErrorHelper.addMessages(errorHandler, 500, "Failed to register user in database.");
            send(errorHandler, userData);
          }

          done(); // Close DB connection.
        });
      } else {
        ErrorHelper.addMessages(errorHandler, 520, "Something went wrong with registration."); // Unknown.

        send(errorHandler, userData);
        done(); // Close DB connection.
      }
    });
  });
};

/**
 * @param data: User login information.
 * @param send: Callback.
 * @return: JSON Webtoken containing user session information.
 **/
exports.login = function (data, send) {
	var email        = data.email;
	var rawPassword  = data.password;
  var errorHandler = { hasErrors: false, messages: [], statusCode: 200 };

	/* Connect to Postgres DB. */
	Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connecting to database. **/
		if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.

			send(errorHandler);
      return;
    }

    var prelimPreparedStatement = "SELECT userId, password " + 
                                  "FROM User_Pers " +
                                  "WHERE email = $1;";
    var inserts           = [ email ];
    var userData          = {};

    /** Query for user information. **/
    client.query(prelimPreparedStatement, inserts, function (err, result) {
      /*** Problem querying database. ***/
      if (err) {
        ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      } else if (result.rows.length === 0) {
        /*** User does not exist. ***/
        ErrorHelper.addMessages(errorHandler, 400, ("User " + email + " does not exist.")); // Bad Request.
      } else if (result.rows.length === 1) {
        /*** User was found. ***/
        userData = result.rows[0];

        /**** Verify their password is correct. ****/
        var userPassword = userData.password;
        delete userData.password; // Don't leave sensitive breadcrumbs.

        if (PasswordHasher.verify(rawPassword, userPassword)) {
          /***** Get the user's name for client purposes. *****/
          var userInfoPreparedStatement = "SELECT nameFirst, nameLast " +
                                          "FROM User_Gen " +
                                          "WHERE userId=$1;"
          var userInfoInserts           = [ userData.userid ];

          client.query(userInfoPreparedStatement, userInfoInserts, function (err, result) {
            done(); // Close DB connection.

            /****** Error querying for user info. ******/
            if (err) {
              ErrorHelper.mergeMessages(errorHandler, 500, err);
            } else if (result.rows.length > 0) {
              /******* User info was found - create a JWT. *******/
              userData  = result.rows[0];
              var token = JWT.sign(userData, jwtSecret, jwtOptions);

              send(errorHandler, token);
              return;
            } else {
              ErrorHelper.addMessages(errorHandler, 520, "Something went wrong while retrieving user info.");
            }

            send(errorHandler, userData);
          });
        } else {
          done(); // Close DB connection.
          ErrorHelper.addMessages(errorHandler, 401, "Incorrect password."); // Unauthorized.
          send(errorHandler, userData);
        }

        return; // Don't make a send requst twice.
      }

      done(); // Close DB connection.
      send(errorHandler, userData);
    });
	});
};
