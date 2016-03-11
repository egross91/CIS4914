'use strict';

var groupsRoom = "/groups";

module.exports = function (io) {
	var groups = io.of(groupsRoom).on('connection', function (socket) {
    socket.on('joingroup', function (data) {
      socket.join(data.groupId);
    });

    socket.on('locationpush', function (data) {
      io.of(groupsRoom).to(data.groupId).emit('locationreceived', data);
    });
  });
};