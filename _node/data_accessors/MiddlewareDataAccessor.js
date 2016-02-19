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

/**
 * @param data: IP address from client.
 * @param send: Callback on successs.
 * @summary: Updates the user's IP address within the database.
 * @note: The JWT is assumed to be good because it _should_ have
 *        went through the checkToken() MiddlewareController first.
 **/
exports.updateIP = function (data, send) {
  var ip           = data.ip;
  var token        = data.jwt;
  var userData     = JWT.decode(token);
  var errorHandler = ErrorHelper.getHandler();

  Postgres.connect(postgresConnectionString, function (err, client, done) {
    if (err) {
      ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
      send(errorHandler);
      done();
    } else {
      var preparedUpdateStatement = "UPDATE User_Device " +
                                    "SET deviceIp=$1 " +
                                    "WHERE userId=$2;";
      var updateInserts           = [ ip, userData.userId ];

      client.query(preparedUpdateStatement, updateInserts, function (err, result) {
        if (err) {
          ErrorHelper.mergeMessages(errorHandler, 500, err); // Internal Server Error.
        }
        
        send(errorHandler);
        done();
      });
    }
  });
};