'use strict';

/**
 * Modules.
 **/
var ErrorHelper = require('../helpers/ErrorHelper');
var UserDA      = require('../data_accessors/UserDataAccessor');
var JWT         = require('jsonwebtoken');

/**
 * Static strings.
 */
var jwtSecret = process.env.MU_JWT_SECRET;

exports.getCurrentUser = function (req, res, err) {
  var handler = ErrorHelper.getHandler();
  var data    = JWT.decode(req.headers.jwt, jwtSecret);
  delete data.iat; delete data.exp;

  UserDA.getCurrentUser(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Retrieves the specified user's friends' information from the DB.
 **/
exports.updateUser = function (req, res, err) {
  var handler     = ErrorHelper.getHandler();
  var data        = JWT.decode(req.headers.jwt, jwtSecret);
  data.updateInfo = req.headers.updateInfo;

  UserDA.updateUser(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Retrieves the specified user's friends' information from the DB.
 **/
exports.getFriends = function (req, res, err) {
  var handler = ErrorHelper.getHandler();
  var data    = JWT.decode(req.headers.jwt, jwtSecret);

  UserDA.getFriends(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Updates the specified user's friends' information in the DB.
 **/
exports.updateFriends = function (req, res, err) {
  var handler    = ErrorHelper.getHandler();
  var data       = JWT.decode(req.headers.jwt, jwtSecret);
  data.friendIds = req.headers.friendids;

  UserDA.updateFriends(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Finds a user based on first and last name.
 **/
exports.findUser = function (req, res, err) {
  var handler    = ErrorHelper.getHandler();
  var data       = {};
  data.nameFirst = req.headers.user.nameFirst;
  data.nameLast  = req.headers.user.nameLast;

  UserDA.findUser(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    req.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};
