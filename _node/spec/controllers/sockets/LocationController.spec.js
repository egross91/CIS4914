'use strict';

/**
 * Modules.
 **/
var io = require('socket.io-client');

describe("Socket Location Controller tests", function () {
  /**
   * Mock strings.
   */
  var host        = "http://localhost:3000";
  var groupsRoom  = "/groups";

  /**
   * Mock objects.
   */
  var testSocket1 = {};
  var testSocket2 = {};
  var testSocket3 = {};

  /**
   * Pre-test(s) setup.
   */
  beforeEach(function (done) {
    testSocket1 = io(host + groupsRoom);
    testSocket2 = io(host + groupsRoom);
    testSocket3 = io(host + groupsRoom);
    testSocket1.on('connect', function () {
      done();
    });
  });

  /**
   * Post-test(s) teardown.
   */
  afterEach(function () {
    testSocket1.disconnect();
    testSocket2.disconnect();
    testSocket3.disconnect();
  });

  it("should connect to the socket server and print 'lol'", function (done) {
    // Mock group location information to be pushed.
    var locationMockGroup1 = {
      latitude: 100.000,
      longitude: -100.000,
      groupId: 1
    };
    var locationMockGroup2 = {
      latitude: 1.000,
      longitude: -1.0000,
      groupId: 2
    };

    // Setup users in groups.
    testSocket1.emit('joingroup', locationMockGroup1);
    testSocket2.emit('joingroup', locationMockGroup1);
    testSocket3.emit('joingroup', locationMockGroup2);
    testSocket1.emit('locationpush', locationMockGroup1);

    // What happens when the information is pushed to group.
    testSocket3.on('locationreceived', function (data) {
      fail("This socket does not belong to group 1.");
    });
    testSocket1.on('locationreceived', function (data) {
      expect(data.longitude).toBe(locationMockGroup1.longitude);
    });
    testSocket2.on('locationreceived', function (data) {
      expect(data.latitude).toBe(locationMockGroup1.latitude);
      done();
    });
  });
});
