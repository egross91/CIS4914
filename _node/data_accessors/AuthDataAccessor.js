var postgres = require('pg');

var postgresConnectionString = process.env.MU_CONN_STR;

exports.login = function (data, send) {
	var email       = data.email;
	var rawPassword = data.password;

	/** Connect to Postgres DB. **/
	postgres.connect(postgresConnectionString, function (err, client, done) {
		if (err) {
			send(err);
    }

		var preparedStatement = "SELECT firstName, lastName " + 
							       	      "FROM Users " + 
                            "WHERE email = $1 AND password = $2";
    var inserts           = [ data.email, data.password ];

    client.query(preparedStatement, inserts, function (err, result) {
      done();

      if (err) {
        send(err);
      } else if (result.rows.length == 0) {
        send({ message: "User " + email + " does not exist." });
      } else {
        /** TODO: Better error handling. **/
        var userData = result.rows[0];

        send(null, userData);
      }
    });
	});
};