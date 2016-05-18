'use strict';

/**
 * Modules.
 **/
var ErrorHelper = require('../helpers/ErrorHelper');
var GroupDA     = require('../data_accessors/GroupDataAccessor');
var JWT         = require('jsonwebtoken');

/**
 * Static strings.
 */
var jwtSecret = process.env.MU_JWT_SECRET;

/**
 * @param req: HTTP request.
 * @param res: HTTP response.
 * @param err: HTTP err.
 * @summary: Retrieves the specified user's groups' information from the DB.
 **/
exports.getGroups = function (req, res, err) {
  var handler = ErrorHelper.getHandler();
  var data    = JWT.decode(req.headers.jwt, jwtSecret);

  GroupDA.getGroups(data, function (err, userData) {
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
 * @summary: Retrieves the group's members from the DB.
 **/
exports.getGroupMembers = function (req, res, err) {
  var handler = ErrorHelper.getHandler();
  var data    = req.params.groupId;

  GroupDA.getGroupMembers(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

exports.getNextGroupId = function (req, res, err) {
  var handler = ErrorHelper.getHandler();

  GroupDA.getNextGroupId(null, function (err, result) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(err);
    } else {
      res.send(result);
    }
  });
};

exports.deleteGroup = function (req, res, err) {
  var handler = ErrorHelper.getHandler();
  var data    = req.params.groupId;

  GroupDA.deleteGroup(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};

exports.getGroupInfo = function (req, res, err) {
  var handler  = ErrorHelper.getHandler();
  var data     = {};
  data.groupId = req.params.groupId;

  GroupDA.getGroupInfo(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  })
};

exports.updateGroupInfo = function (req, res, err) {
  var handler    = ErrorHelper.getHandler();
  var data       = {};
  data.groupName = req.headers.groupname;
  data.groupDesc = req.headers.groupdesc;
  data.groupId   = req.params.groupId;

  GroupDA.updateGroupInfo(data, function (err, userData) {
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
exports.updateGroupMembers = function (req, res, err) {
  var handler       = ErrorHelper.getHandler();
  var data          = {};
  data.groupMembers = req.headers.groupmembers;
  data.groupId      = req.params.groupId;

  GroupDA.updateGroupMembers(data, function (err, userData) {
    ErrorHelper.mergeMessages(handler, err.statusCode, err);
    res.statusCode = handler.statusCode;

    if (err.hasErrors) {
      res.send(handler);
    } else {
      res.send(userData);
    }
  });
};