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
	var errorHandler = { hasErrors: false, messages: [], statusCode: 200 };

  /* Connect to database. */
  Postgres.connect(postgresConnectionString, function (err, client, done) {
    /** Problem connection to database. **/
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err);
      ErrorHelper.addMessages(errorHandler, errorHandler.statusCode, "Failed to connect to database.");

      send(errorHandler);
    } else {
      var locationPreparedStatement = "SELECT longitude, latitude " +
                                      "FROM User_Loc " +
                                      "WHERE userId=$1;";
      var inserts                   = [ userId ];
      var userData                  = {};

      client.query(locationPreparedStatement, inserts, function (err, result) {
        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err);

          send(errorHandler, userData);
          done(); // Close DB Connection.
        } else if (result.rows.length === 0) {
          ErrorHelper.addMessages(errorHandler, 204, ("No location for userId: " + userId + "."));

          send(errorHandler, userData);
          done();
        } else if (result.rows.length === 1) {
          userData = result;
          send(errorHandler, userData);
          done();
        } else {
          ErrorHelper.addMessages(errorHandler, 520, "Something went wrong with getting location data.");
          send(errorHandler, userData);
          done();
        }
      });
    }
  });
};