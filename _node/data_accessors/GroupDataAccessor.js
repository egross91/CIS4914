'use strict';

/**
 * Modules.
 **/
var Postgres    = require('pg');
var ErrorHelper = require('../helpers/ErrorHelper');

/**
 * Static strings.
 **/
var postgresConnectionString = process.env.MU_CONN_STR;

/**
 * @param data: JWT from user request.
 * @param send: Callback - to be called on error or success.
 * @summary: Retrieves the specified user's groups' information from the DB.
 **/
exports.getGroups = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = data;

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(err);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM user_groups_info($1::int);";
      var inserts           = [ userData.userId ];

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Close DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);
          send(errorHandler);
        } else {
          if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, "No groups were found for " + userData.userId + ".");
            send(errorHandler);
          } else {
            send(errorHandler, result.rows);
          }
        }
      });
    }
  });
};


/**
 * @param data: GroupId user request.
 * @param send: Callback - to be called on error or success.
 * @summary: Retrieves the group's information (members, locations, etc.) from the DB.
 **/
exports.getGroup = function (data, send) {
  var groupId      = data;
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(errorHandler);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM get_group_info($1::bigint);";
      var inserts = [ groupId ];

      client.query(preparedStatement, inserts, function (err, result) {
        done();

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);
          send(errorHandler);
        } else {
          if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, "No members for group " + groupId + ".");
            send(errorHandler);
          } else {
            send(errorHandler, result.rows);
          }
        }
      })
    }
  });
};

exports.getNextGroupId = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(errorHandler, false);
    } else {
      var preparedStatement = "SELECT groupId " +
                              "FROM groups_info " +
                              "ORDER BY groupId DESC " +
                              "LIMIT 1;";

      client.query(preparedStatement, function (err, result) {
        done();

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);
          send(errorHandler, false);
        } else {
          var groupIdVal  = parseInt(result.rows[0].groupid);
          var nextGroupId = groupIdVal + 1;
          var data        = { groupid: nextGroupId };

          send(errorHandler, data);
        }
      })
    }
  });
};

exports.deleteGroup = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var groupId      = data;

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler, false);
    } else {
      var preparedStatement = "DELETE FROM groups_info " +
                              "WHERE groupid=$1::bigint;";
      var inserts           = [ groupId ];

      client.query(preparedStatement, inserts, function (err, result) {
        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
          send(errorHandler, false);
        } else {
          send(errorHandler, true);
        }
      });
    }
  });
};

exports.updateGroupInfo = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler, false);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM groups_info_upsert($1::bigint, $2::text, $3::text);";
      var inserts           = [ data.groupId, data.groupName, data.groupDesc ];

      client.query(preparedStatement, inserts, function (err, result) {
        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
          send(errorHandler, false);
        } else {
          send(errorHandler, true);
        }
      });
    }
  });
};

/**
 * @param data: Contains both headers and the groupId from params.
 * @param send: Callback - to be called on error or success.
 * @summary: Update the specified user's group information in the DB.
 **/
exports.updateGroupMembers = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var groupId      = data.groupId;
  var groupMembers = '{' + data.groupMembers + '}';

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM user_groups_upsert($1::bigint, $2::int[]);";
      var inserts           = [ groupId, groupMembers ];

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Close DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
        }

        send(errorHandler);
      });
    }
  });
};