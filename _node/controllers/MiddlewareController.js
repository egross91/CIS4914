'use strict';

/**
 * Modules
 **/
var JWT          = require('jsonwebtoken');
var ErrorHelper  = require('../helpers/ErrorHelper');
var MiddlewareDA = require('../data_accessors/MiddlewareDataAccessor');

/**
 * Static strings.
 **/
var jwtSecret = process.env.MU_JWT_SECRET;

/**
 * This API is meant to be used for middleware verification of 
 * requests to sensitive information such as (but not limited to)
 * users' location, names, etc.
 **/

/**
 * @param req: User HTTP request.
 * @param res: HTTP response.
 * @param next: The 'next' function in the chain of command.
 * @summary: Validates that the JSONWebToken is supplied AND that
 *           it is not expired.
 **/
exports.checkToken = function (req, res, next) {
  var jwtData = req.headers.jwt;
	var handler = ErrorHelper.getHandler();

  if (!jwtData) {
		ErrorHelper.addMessages(handler, 400, "No JWT data was supplied.");
		res.send(handler);
	} else {
    JWT.verify(jwtData, jwtSecret, function (err, decoded) {
      if (err) {
        ErrorHelper.mergeMessages(handler, 407, err); // Proxy Authentication Required.
        res.send(handler);
      } else {
        if (decoded) {
          /* Request was good. */
          next();
        } else {
          /* Something went wrong during validation. */
          ErrorHelper.addMessages(hander, 407, "Could not verify JWT.");
          res.send(handler);
        }
      }
    });
  }
};

/**
 * @param req: User HTTP request.
 * @param res: HTTP response.
 * @param next: The 'next' function in the chain of command.
 * @summary: Updates the user's IP address in the DB.
 **/
exports.updateIP = function (req, res, next) {
  var data    = req.headers;
  var handler = ErrorHelper.getHandler();

  if (!data.ip) {
    ErrorHelper.addMessages(handler, 412, "No IP was found in request."); // Precondition Failed

    res.send(handler);
  } else {
    MiddlewareDA.updateIP(data, function (err, data) {
      if (err.hasErrors) {
        ErrorHelper.mergeMessages(handler, err.statusCode, err);
        res.send(handler);
      } else {
        /* Success. */
        next();
      }
    });
  }
};