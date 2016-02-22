'use strict';

/**
 * Modules.
 **/
var ErrorHelper = require('../helpers/ErrorHelper');
var UserDA      = require('../data_accessors/UserDataAccessor');

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