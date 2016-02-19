'use strict';

/**
 * Modules.
 **/
var LocationDA  = require('../data_accessors/LocationDataAccessor');
var ErrorHelper = require('../helpers/ErrorHelper');

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Retrieves the specified user's location from the DB.
 **/
exports.getUserLocation = function (req, res, err) {
	var handler = { hasErrors: false, messages: [], statusCode: 200 };
	var data    = req.params;

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