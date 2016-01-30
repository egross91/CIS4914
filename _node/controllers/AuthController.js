'use strict';

var AuthDA      = require('../data_accessors/AuthDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.register = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.headers;

	AuthDA.register(data, function (err, userData) {
		// TODO: Fix DRY.
		res.statusCode = handler.statusCode;

		if (err.hasErrors) {
			ErrorHelper.mergeMessages(handler, err.statusCode, err);
			res.send(handler);
		} else {
			res.send(userData);
		}
	});
};

exports.login = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.headers;
	
	AuthDA.login(data, function (err, userData) {
		res.statusCode = handler.statusCode;

		if (err.hasErrors) {
			ErrorHelper.mergeMessages(handler, err.statusCode, err);
			res.send(handler);
		} else {
			res.send(userData);
		}
	});
};
