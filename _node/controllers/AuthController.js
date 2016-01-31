'use strict';

var AuthDA      = require('../data_accessors/AuthDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.register = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.headers;

	AuthDA.register(data, function (err, userData) {
		// TODO: Fix DRY.
		ErrorHelper.mergeMessages(handler, err.statusCode, err);
		res.statusCode = handler.statusCode;

		if (err.hasErrors) {
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
		ErrorHelper.mergeMessages(handler, err.statusCode, err);
		res.statusCode = handler.statusCode;

		if (err.hasErrors) {
			res.send(handler);
		} else {
			res.send(userData);
		}
	});
};
