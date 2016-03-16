'use strict';

/**
 * Modules.
 */
var LocationDA = require('../../data_accessors/LocationDataAccessor');
var JWT        = require('jsonwebtoken');

/**
 * Static strings.
 */
var groupsRoom = "/groups";

module.exports = function (io) {
	var groups = io.of(groupsRoom).on('connection', function (socket) {
    socket.on('joingroup', function (data) {
      socket.join(data.groupId);
    });

    // Location synchronizing.
    socket.on('locationpush', function (data) {
      formatData(data);

      // Update the location in the database.
      LocationDA.updateUserLocation(data, function (err, result) {
      	if (err.hasErrors) {
      		io.of(groupsRoom).to(socket.id).emit('failedlocationupdate', err);
      	}

        // Send location to everyone within the room (group on client side).
        santizeData(data);
        io.of(groupsRoom).to(data.groupId).emit('locationreceived', data);
      });
    });

    // Instant messaging.
    socket.on('messagepush', function (data) {
      formatData(data);
      santizeData(data);

      io.of(groupsRoom).to(data.groupId).emit('messagereceived', data);
    });

    /**
     * Helper methods.
     */
    var formatData = function (data) {
      var userData = JWT.decode(data.jwt);

      data.sender = {
        nameFirst: userData.namefirst,
        nameLast: userData.namelast
      };
    };

    var santizeData = function (data) {
      delete data.jwt;
    }
  });
};