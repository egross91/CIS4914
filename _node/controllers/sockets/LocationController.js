'use strict';

/**
 * Modules.
 */
var LocationDA = require('../../data_accessors/LocationDataAccessor');

/**
 * Static strings.
 */
var groupsRoom = "/groups";

module.exports = function (io) {
	var groups = io.of(groupsRoom).on('connection', function (socket) {
    socket.on('joingroup', function (data) {
      socket.join(data.groupId);
    });

    socket.on('locationpush', function (data) {
      // Send location to everyone within the room (group on client side).
      io.of(groupsRoom).to(data.groupId).emit('locationreceived', data);

      // Update the location in the database.
      LocationDA.updateUserLocation(data, function (err, result) {
      	if (err.hasErrors) {
      		io.of(groupsRoom).to(socket.id).emit('failedlocationupdate', err);
      	}
      });
    });
  });
};