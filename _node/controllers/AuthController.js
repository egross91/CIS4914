'use strict';

/**
 * Modules.
 */
var AuthDA      = require('../data_accessors/AuthDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.register = function (req, res, err) {
	var handler   = ErrorHelper.getHandler();
	var data      = {};
	data.email    = req.headers.email;
	data.password = req.headers.password;

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
	var handler   = ErrorHelper.getHandler();
	var data      = {};
	data.email    = req.headers.email;
	data.password = req.headers.password;
	
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
