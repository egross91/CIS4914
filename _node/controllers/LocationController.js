'use strict';

var LocationDA = require('../data_accessors/LocationDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

exports.getUserLocation = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data = req.params;

	LocationDA.getUserLocation(data, function (err, userData) {
		ErrorHelper.mergeMessages(handler, err.statusCode, err);
		res.statusCode = handler.statusCode;

		if (err.hasErrors) {
			res.send(handler);
		} else {
			res.send(userData);
		}
	});
};