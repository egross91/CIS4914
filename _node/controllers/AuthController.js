'use strict';

var AuthDA      = require('../data_accessors/AuthDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.register = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.headers;

	AuthDA.register(data, function (err, userData) {
		// TODO: Fix DRY.
		if (err.hasErrors) {
			ErrorHelper.mergeMessages(handler, err);
		}

		res.statusCode = handler.statusCode;
		res.send(handler);
	});
};

exports.login = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.headers;
	
	AuthDA.login(data, function (err, userData) {
		if (err.hasErrors) {
			ErrorHelper.mergeMessages(handler, err);
		}

		res.statusCode = handler.statusCode;
		res.send(handler);
	});
};
