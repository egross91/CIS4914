'use strict';

/**
 * Modules.
 **/
var Postgres       = require('pg');
var JWT            = require('jsonwebtoken');
var PasswordHasher = require('password-hash');
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
  var errorHandler = ErrorHelper.getHandler();

  /* Connect to database. */
  Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connecting to database. **/
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Failed to connect to database.");

      send(errorHandler);
      done();
    } else {
      var queryPreparedStatement = "SELECT email " +
                                   "FROM User_Pers " +
                                   "WHERE email = $1;";
      var queryInserts           = [ data.email ];
      var userData               = {};

      /** Check if email exists. **/
      client.query(queryPreparedStatement, queryInserts, function (err, result) {
        if (err) {
          /*** Problem while querying for email. ***/
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.

          send(errorHandler, userData);
          done(); // Close DB connection.
        }
        else {
          if (result.rows.length > 0) {
            /*** User exists. ***/
            ErrorHelper.addMessages(errorHandler, 409, (data.email + " already exists! :'(")); // Conflict.

            send(errorHandler, userData);
            done(); // Close DB connection.
          } else if (result.rows.length === 0) {
            /*** User does NOT exist. ***/
            var setupPreparedStatement = "SELECT * " +
                                         "FROM setup_user($1, $2) " + 
                                         "AS f(success);";
            var saltedPassword         = PasswordHasher.generate(data.password);
            var insertInserts          = [ data.email, saltedPassword ];

            /*** Create new user in database. **/
            client.query(setupPreparedStatement, insertInserts, function (err, result) {
              /**** Problem inserting into database. ****/
              if (err) {
                ErrorHelper.mergeMessages(errorHandler, 500, err);
                ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Error querying.");
              } else {
                if (result.rows[0].success !== -1) {
                  // Set token data.
                  userData.email  = userData.nameFirst = data.email;
                  userData.userId = result.rows[0].success;
                  
                  var token = JWT.sign(userData, jwtSecret, jwtOptions);
                  send(errorHandler, token);
                } else {
                  ErrorHelper.addMessages(errorHandler, 500, "Failed to register user in database.");
                  send(errorHandler, userData);
                }
              }
              done(); // Close DB connection.
            });
          } else {
            ErrorHelper.addMessages(errorHandler, 520, "Something went wrong with registration."); // Unknown.

            send(errorHandler, userData);
            done(); // Close DB connection.
          }
        }
      });
    }
  });
};

/**
 * @param data: User login information.
 * @param send: Callback.
 * @return: JSON Webtoken containing user session information.
 **/
exports.login = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();

	/* Connect to Postgres DB. */
	Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connecting to database. **/
		if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
			send(errorHandler);
      done(); // Close DB connection.
    } else {
      var prelimPreparedStatement = "SELECT userId, password " + 
                                    "FROM User_Pers " +
                                    "WHERE email = $1;";
      var inserts           = [ data.email ];
      var userData          = {};

      /** Query for user information. **/
      client.query(prelimPreparedStatement, inserts, function (err, result) {
        /*** Problem querying database. ***/
        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
          send(errorHandler);
          done();
        } else {
          if (result.rows.length === 0) {
            /*** User does not exist. ***/
            ErrorHelper.addMessages(errorHandler, 400, ("User " + data.email + " does not exist.")); // Bad Request.
            send(errorHandler);
            done();
          } else if (result.rows.length === 1) {
            /*** User was found. ***/
            userData = result.rows[0];

            /**** Verify their password is correct. ****/
            var userPassword = userData.password;
            delete userData.password; // Don't leave sensitive breadcrumbs.

            if (PasswordHasher.verify(data.password, userPassword)) {
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
                } else {
                  if (result.rows.length > 0) {
                    /******* User info was found - create a JWT. *******/
                    userData        = result.rows[0];
                    userData.userId = userInfoInserts[0];
                    var token       = JWT.sign(userData, jwtSecret, jwtOptions);

                    send(errorHandler, token);
                    return;
                  } else {
                    ErrorHelper.addMessages(errorHandler, 520, "Something went wrong while retrieving user info.");
                  }
                }

                send(errorHandler, userData);
              });
            } else {
              ErrorHelper.addMessages(errorHandler, 401, "Incorrect password."); // Unauthorized.
              send(errorHandler, userData);
              done(); // Close DB connection.
            }
          } else {
            send(errorHandler, userData);
            done(); // Close DB connection.
          }
        }
      });
    }
	});
};
