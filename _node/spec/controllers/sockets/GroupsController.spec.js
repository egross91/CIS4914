'use strict';

/**
 * Modules.
 **/
var io = require('socket.io-client');

describe("Socket Location Controller tests", function () {
  /**
   * Mock strings.
   */
  var testJWT     = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lZmlyc3QiOiJ0ZXN0QHRlc3QubmV0IiwibmFtZWxhc3QiOm51bGwsInVzZXJJZCI6IjE4IiwiaWF0IjoxNDU3NzM2Mzk2fQ.eU3WqLSKTr-Y3_wcnxb4NWTQRNycHgfjC0uS6BTWazg";
  var host        = "http://localhost:3000";
  var groupsRoom  = "/groups";

  var testMessage = "Hello world!";

  /**
   * Mock objects.
   */
  var testSocket1 = {};
  var testSocket2 = {};
  var testSocket3 = {};
  var testUser1   = {
    nameFirst: "test@test.net",
    nameLast:  null
  }

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

  it("should connect to the socket server and emit to appropriate sockets within same group", function (done) {
    // Mock group location information to be pushed.
    var locationMockGroup1 = {
      latitude: 100.111,
      longitude: -100.111,
      jwt: testJWT,
      groupId: 1
    };
    var locationMockGroup2 = {
      latitude: 1.999,
      longitude: -1.999,
      jwt: testJWT,
      groupId: 2
    };
    var messageMockGroup1 = {
      groupId: 1,
      jwt: testJWT,
      message: testMessage
    };


    // Setup users in groups.
    testSocket1.emit('joingroup', locationMockGroup1);
    testSocket2.emit('joingroup', locationMockGroup1);
    testSocket3.emit('joingroup', locationMockGroup2);
    testSocket1.emit('locationpush', locationMockGroup1);
    testSocket1.emit('messagepush', messageMockGroup1);

    // What happens when the information is pushed to group.
    testSocket3.on('locationreceived', function (data) {
      fail("This socket does not belong to group 1.");
    });
    testSocket1.on('locationreceived', function (data) {
      expect(data.longitude).toBe(locationMockGroup1.longitude);
    });
    testSocket2.on('locationreceived', function (data) {
      expect(data.latitude).toBe(locationMockGroup1.latitude);
    });

    testSocket2.on('messagereceived', function (data) {
      expect(data.message).toBe(messageMockGroup1.message);
      expect(data.sender.nameFirst).toBe(testUser1.nameFirst);
      expect(data.sender.nameLast).toBe(testUser1.nameLast);
      done();
    });
  });
});
