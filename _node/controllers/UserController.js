'use strict';

/**
 * Modules.
 **/
var ErrorHelper = require('../helpers/ErrorHelper');
var UserDA      = require('../data_accessors/UserDataAccessor');

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Retrieves the specified user's friends' information from the DB.
 **/
exports.getFriends = function (req, res, err) {
  var data    = req.headers.jwt;
  var handler = ErrorHelper.getHandler();

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
  var data    = req.headers;
  var handler = ErrorHelper.getHandler();

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
 * @summary: Retrieves the specified user's groups' information from the DB.
 **/
exports.getGroups = function (req, res, err) {
  var data    = req.headers.jwt;
  var handler = ErrorHelper.getHandler();

  UserDA.getGroups(data, function (err, userData) {
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
 * @summary: Retrieves the group's information (members, locations, etc.) from the DB.
 **/
exports.getGroup = function (req, res, err) {
  var data    = req.params.groupId;
  var handler = ErrorHelper.getHandler();

  UserDA.getGroup(data, function (err, userData) {
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
 * @summary: Update the specified user's group information in the DB.
 **/
exports.updateGroup = function (req, res, err) {
  var data     = req.headers;
  var handler  = ErrorHelper.getHandler();
  data.groupId = req.params.groupId;

  UserDA.updateGroup(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};