'use strict';

/**
 * Module under test.
 **/
var UserController = require('../../controllers/UserController');

describe("Unit tests for the User Controller", function () {
  /**
   * Static strings.
   **/
  var mockJwt       = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lZmlyc3QiOiJ0ZXN0QHRlc3QubmV0IiwibmFtZWxhc3QiOm51bGwsInVzZXJJZCI6IjE4IiwiaWF0IjoxNDU2MjAyMDM0fQ.lDn0aw7Zpo0knv8Xcb6fQWKZOzjK0ZHvuZiRqr9oLG8";
  var mockGroupId   = 1;
  var mockGroupDesc = "world";
  var mockGroupName = "hello";

  var mockGroupMemberOne = 18;
  var mockGroupMemberTwo = 19;

	/**
   * Mock objects.
   **/
  var result         = {  };
  var requestMockObj = {
    headers: {
      jwt: mockJwt,
      userid: mockGroupMemberOne
    }, params: {
      groupId: mockGroupId
    }
  };
  var responseMockObj = {
    send: function (data) {
      result = data;
    }
  };

  // TODO: Create beforeAll() & afterAll() to create and clean mock data in DB.

  it("should return the group information when sending the 'groupid' - getGroup().", function (done) {
    setTimeout(function () {
      UserController.getGroup(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result[0].userid).toBe(mockGroupMemberOne);
        expect(result[1].userid).toBe(mockGroupMemberTwo);
        done();
      }, 400)
    }, 100);
  });

  it("should return 204 error if no one is found - getGroup().", function (done) {
    requestMockObj.params.groupId = -1; // Should never exist.

    setTimeout(function () {
      UserController.getGroup(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(204); // No Content.
        done();
      }, 400);
    }, 100);
  });

  it("should return a 500 error if an integer is not supplied for 'groupid' - getGroup().", function (done) {
    requestMockObj.params.groupId = "fail";

    setTimeout(function () {
      UserController.getGroup(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(500); // Internal Server Error.
        done();
      }, 400);
    }, 100);
  });

  it("should return any friends from for a user - getFriends().", function (done) {
    setTimeout(function () {
      UserController.getFriends(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.length).toBe(1); // The number of groups for the mock user.
        expect(result[0].namefirst).toBeTruthy(); // Does not come back in a particular order.
        done();
      }, 400);
    }, 100);
  });
});