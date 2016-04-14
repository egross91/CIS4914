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

exports.updateUser = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = data;

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      send(errorHandler);
    } else {
      var preparedStatement = "UPDATE user_gen " +
                              "SET nameFirst=$1, nameLast=$2 " +
                              "WHERE userId=$3::int;";
      var inserts           = [ userData.updateInfo.nameFirst, userData.updateInfo.nameLast, userData.userId ];

      client.query(preparedStatement, inserts, function (err, result) {
        done();

        if (err) {
          ErrorHelper.mergeMessasges(errorHandler, 500, err); // Internal Server Error.
          send(errorHandler, false);
        } else {
          send(errorHandler, true)
        }
      });
    }
  });
};

/**
 * @param data: JWT from user request.
 * @param send: Callback - to be called on error or success.
 * @summary: Retrieves the specified user's friends' information from the DB.
 **/
exports.getFriends = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = data;

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

/**
 * @param data: Headers from HTTP request..
 * @param send: Callback - to be called on error or success.
 * @summary: Updates the specified user's friends' information in the DB.
 **/
exports.updateFriends = function (data, send) {
  var errorHandler = ErrorHelper.getHandler();
  var userData     = data;
  var friendIds    = '{' + data.friendIds + '}';

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler);
    } else {
      var preparedStatement = "SELECT * " +
                              "FROM user_friends_upsert($1::int, $2::int[]);";
      var inserts           = [ data.userId, friendIds ];

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Clone DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
        }

        send(errorHandler);
      });
    }
  });
};

exports.findUser = function (data, send) {
  var nameFirst    = data.nameFirst || "";
  var nameLast     = data.nameLast || "";
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler);
    } else {
      var inserts           = [ ];
      var preparedStatement = "SELECT * " +
                              "FROM user_gen " +
                              "WHERE " + formatNameClause(nameFirst, nameLast, inserts) + ";";
      var userData          = {};

      client.query(preparedStatement, inserts, function (err, result) {
        done(); // Close DB connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
        } else {
          if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, "User not found.");
          } else if (result.rows.length > 0) {
            userData.users = result.rows;
          } else {
            ErrorHelper.addMessages(errorHandler, 520, "Something went wrong."); // Unknown Error.
          }
        }

        send(errorHandler, userData);
      });
    }
  });
};

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
                              "FROM get_group_info($1::int);";
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
          var nextGroupId = result.rows[0].groupid + 1;
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
                              "WHERE groupid=$1::int;";
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
                              "FROM groups_info_upsert($1::int, $2::text, $3::text);";
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
                              "FROM user_groups_upsert($1::int, $2::int[]);";
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

/**
 * Helper methods.
 */
var formatNameClause = function (nameFirst, nameLast, inserts) {
    var opts = [];

    if (nameFirst) {
      opts.push("nameFirst=$" + (inserts.length + 1) + "::text");
      inserts.push(nameFirst);
    } else {
      opts.push("nameFirst is null");
    }

    if (nameLast) {
      opts.push("nameLast=$" + (inserts.length + 1) + "::text");
      insert.push(nameLast);
    } else {
      opts.push("nameLast is null");
    }

    return opts.join(" AND ");
  };