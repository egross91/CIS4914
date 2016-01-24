var postgres    = require('pg');
var ErrorHelper = require('../helpers/ErrorHelper');

var postgresConnectionString = process.env.MU_CONN_STR;

exports.login = function (data, send) {
	var email        = data.email;
	var rawPassword  = data.password;
  var errorHandler = { hasErrors: false, messages: [], statusCode: 200 };

	/** Connect to Postgres DB. **/
	postgres.connect(postgresConnectionString, function (err, client, done) {
		if (err) {
      ErrorHelper.mergeMessages(errorHandler, err);
      errorHandler.statusCode = 400;

			send(errorHandler);
    }

		var preparedStatement = "SELECT firstName, lastName " + 
							       	      "FROM Users " + 
                            "WHERE email = $1 AND password = $2";
    var inserts           = [ data.email, data.password ];

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