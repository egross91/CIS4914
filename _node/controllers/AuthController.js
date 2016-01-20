'use strict';

var AuthDA = require('../data_accessors/AuthDataAccessor');

exports.login = function (req, res, err) {
	var response = { hasErrors: false, messages: [] };
	var data     = req.body;
	
	AuthDA.login(data, function(err, data) {
		if (err) {
			response.hasErrors = true;
			/** TODO: Add ErrorHelper **/
			// ErrorHelper.mergeMessages(response.messages, err);

			// res.statusCode = response.statusCode;
		} else {
			res.statusCode = 200;
		}
		res.send(response);
	});
};