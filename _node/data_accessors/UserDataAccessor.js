'use strict';

/**
 * Modules.
 **/
var Postgres    = require('pg');
var JWT         = require('jsonwebtoken');
var ErrorHelper = require('../helpers/ErrorHelper');

/**
 * Static strings.
 **/
 var postgresConnectionString = process.env.MU_CONN_STR;
 var jwtSecret                = process.env.MU_JWT_SECRET;

exports.getFriends = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = JWT.decode(data, jwtSecret);

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(errorHandler);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM user_friends_data($1::int);";
      var inserts           = [ userData.userId ];

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Close DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);
          send(errorHandler);
        }
        else {
          if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, "No friends were found for " + userData.userid + " lol.");
            send(errorHandler);
          } else {
            send(errorHandler, result.rows);
          }
        }
      });
    }
  });
};

exports.getGroups = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = JWT.decode(data, jwtSecret);

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(err);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM user_groups_info($1);";
      var inserts           = [ userData.userId ];

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Close DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);
          send(errorHandler);
        } else {
          if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, "No groups were found for " + userData.userId + ".");
          } else {
            send(errorHandler, result.rows);
          }
        }
      });
    }
  });
};