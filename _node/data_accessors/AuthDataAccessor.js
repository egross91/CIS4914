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
      ErrorHelper.mergeMessages(errorHandler, err);
      errorHandler.statusCode = 500; // Internal Server Error.

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
      done();

      if (result.rows.length !== 0) {
        if (err) {
          /*** Problem while querying for email. ***/
          ErrorHelper.mergeMessages(errorHandler, err);
          errorHandler.statusCode = 500; // Internal Server Error.
        } else if (result.rows.length > 0) { 
          /*** User exists. ***/
          ErrorHelper.addMessages(errorHandler, (email + " already exists! :'("));
          errorHandler.statusCode = 409; // Conflict.
        }

        send(errorHandler, userData);
      } else { // if (result.rows.length === 0)
        /*** User does NOT exist. ***/
        var insertPreparedStatement = "INSERT INTO User_Pers (email, password) " +
                                      "VALUES ($1, $2);";
        var saltedPassword          = PasswordHasher.generate(rawPassword);
        var insertInserts           = [ email, saltedPassword ];

        /*** Create new user in database. **/
        client.query(insertPreparedStatement, insertInserts, function (err, result) {
          /**** Problem inserting into database. ****/
          if (err) {
            ErrorHelper.mergeMessages(errorHandler, err);
            errorHandler.statusCode = 500;
          } else if (result.rows.length === 0) {
            /**** Insert was good. ****/
            // TODO: JWT.
            // userData.jwt = generateJwt();
          }

          send(errorHandler, userData);
        });
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
      ErrorHelper.mergeMessages(errorHandler, err);
      errorHandler.statusCode = 500; // Internal Server Error.

			send(errorHandler);
      return;
    }

    var preparedStatement = "SELECT nameFirst, nameLast " + 
							       	      "FROM User_Gen " + 
                            "WHERE userId IN (SELECT userId " + 
                                             "FROM User_Pers " +
                                             "WHERE email = $1 AND password = $2);";
    var saltedPassword    = PasswordHasher.generate(rawPassword);
    var inserts           = [ email, saltedPassword ];
    var userData          = {};

    /** Query for user information. **/
    client.query(preparedStatement, inserts, function (err, result) {
      done();

      /*** Problem querying database. ***/
      if (err) {
        ErrorHelper.mergeMessages(errorHandler, err);
        errorHandler.statusCode = 500; // Internal Server Error.
      } else if (result.rows.length === 0) {
        /*** User does not exist. ***/
        ErrorHelper.addMessages(errorHandler, ("User " + email + " does not exist."));
        errorHandler.statusCode = 400; // Bad Request.
      } else if (result.rows.length === 1) {
        /*** User was found. ***/
        userData = result.rows[0];

        // TODO: JWT.
        // userData.jwt = generateJwt();
      }

      send(errorHandler, userData);
    });
	});
};