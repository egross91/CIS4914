'use strict';

var Postgres       = require('pg');
var PasswordHasher = require('password-hash');
var ErrorHelper    = require('../helpers/ErrorHelper');

var postgresConnectionString = process.env.MU_CONN_STR;

exports.login = function (data, send) {
	var email        = data.email;
	var rawPassword  = data.password;
  var errorHandler = { hasErrors: false, messages: [], statusCode: 200 };

	/** Connect to Postgres DB. **/
	Postgres.connect(postgresConnectionString, function (err, client, done) {
		if (err) {
      ErrorHelper.mergeMessages(errorHandler, err);
      errorHandler.statusCode = 400;

			send(errorHandler);
    }

    var preparedStatement = "SELECT nameFirst, nameLast " + 
							       	      "FROM User_Gen " + 
                            "WHERE userId IN (SELECT userId " + 
                                             "FROM User_Pers " +
                                             "WHERE email = $1 AND password = $2);";
    var saltedPassword    = PasswordHasher.generate(rawPassword);
    var inserts           = [ email, saltedPassword ];

    client.query(preparedStatement, inserts, function (err, result) {
      done();

      if (err) {
        ErrorHelper.mergeMessages(errorHandler, err);
        errorHandler.statusCode = 500;

        send(errorHandler);
      } else if (result.rows.length == 0) {
        ErrorHelper.addMessages(errorHandler, ("User " + email + " does not exist."));
        errorHandler.statusCode = 400;

        send(errorHandler);
      } else {
        /** TODO: Better error handling. **/
        var userData = result.rows[0];

        send(errorHandler, userData);
      }
    });
	});
};