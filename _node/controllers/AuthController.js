'use strict';

var AuthDA      = require('../data_accessors/AuthDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.login = function (req, res, err) {
	var response = { hasErrors: false, messages: [] };
	var data     = req.body;
	
	AuthDA.login(data, function(err, data) {
		if (err.hasErrors) {
			/** TODO: Add ErrorHelper **/
			ErrorHelper.mergeMessages(response, err);

			res.statusCode = response.statusCode;
		} else {
			res.statusCode = 200;
		}

		res.send(response);
	});
};