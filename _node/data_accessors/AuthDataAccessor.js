'use strict';

/**
 * Modules.
 **/
var Postgres       = require('pg');
var PasswordHasher = require('password-hash');
var ErrorHelper    = require('../helpers/ErrorHelper');

/**
 * Static strings.
 **/
var postgresConnectionString = process.env.MU_CONN_STR;

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
        done();
        return;
      }

      if (result.rows.length > 0) {
        /*** User exists. ***/
        ErrorHelper.addMessages(errorHandler, 409, (email + " already exists! :'(")); // Conflict.

        send(errorHandler, userData);
        done();
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
          } else if (result.rows.length === 0) {
            /**** Insert was good. ****/
            // TODO: JWT.
            // userData.jwt = generateJwt();
          }

          send(errorHandler, userData);
          done();
        });
      } else {
        ErrorHelper.addMessages(errorHandler, 520, "Something went wrong with registration."); // Unknown.

        send(errorHandler, userData);
        done();
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
      done();

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
        if (PasswordHasher.verify(rawPassword, userData.password)) {
          // TODO: JWT.
          // userData.jwt = generateJwt();
        } else {
          ErrorHelper.addMessages(errorHandler, 401, "Incorrect password."); // Unauthorized.
        }
      }

      send(errorHandler, userData);
    });
	});
};