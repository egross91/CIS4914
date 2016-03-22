'use strict';

/**
 * Modules.
 */
var Postgres    = require('pg');
var ErrorHelper = require('../helpers/ErrorHelper');

/**
 * Static strings.
 **/ 
var postgresConnectionString = process.env.MU_CONN_STR;

/**
 * @param data: Data from user request.
 * @param send: Callback.
 * @summary: Gets the location for the userId that
 *           is supplied in the @data.
 **/
exports.getUserLocation = function (data, send) {
  var userId       = data.userId;
	var errorHandler = ErrorHelper.getHandler();

  /* Connect to database. */
  Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connection to database. **/
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Failed to connect to database.");
      send(errorHandler);
      done();
    } else {
      var locationPreparedStatement = "SELECT longitude, latitude " +
                                      "FROM User_Loc " +
                                      "WHERE userId=$1;";
      var inserts                   = [ userId ];
      var userData                  = {};

      client.query(locationPreparedStatement, inserts, function (err, result) {
        done(); // Close DB Connection.

        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
        } else {
           if (result.rows.length === 0) {
            ErrorHelper.addMessages(errorHandler, 204, ("No location for userId: " + userId + ".")); // No Content.
          } else if (result.rows.length === 1) {
            userData = result;
          } else {
            ErrorHelper.addMessages(errorHandler, 520, "Something went wrong with getting location data."); // Unknown Error.
          }
        }

        send(errorHandler, userData);
      });
    }
  });
};

/**
 * @param data: Data from user request.
 * @param send: Callback.
 * @summary: Updates the location for the userId that
 *           is supplied in the @data.
 **/
exports.updateUserLocation = function (data, send) {
  var userData     = {};
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler);
    } else {
      var preparedStatement = "SELECT * " + 
                              "FROM user_location_upsert($1::int, $2::numeric, $3::numeric);";
      var inserts           = [ data.userId, data.longitude, data.latitude ];

      client.query(preparedStatement, inserts, function (err, result) {
        done();

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
