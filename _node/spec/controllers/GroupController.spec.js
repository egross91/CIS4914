'use strict';

/**
 * Module under test.
 **/
var GroupController = require('../../controllers/GroupController');

describe("Unit Tests for Group Controller", function () {
  /**
   * Static strings.
   **/
  var mockJwt        = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lZmlyc3QiOiJ0ZXN0QHRlc3QubmV0IiwibmFtZWxhc3QiOm51bGwsInVzZXJJZCI6IjE4IiwiaWF0IjoxNDU3NzM2Mzk2fQ.eU3WqLSKTr-Y3_wcnxb4NWTQRNycHgfjC0uS6BTWazg";
  var mockGroupId    = 1;
  var mockGroupDesc  = "world";
  var mockGroupName  = "hello";
  var mockNameFirst1 = "test@test.net";
  var mockNameLast1  = null;

  var mockGroupMemberOne = 18;
  var mockGroupMemberTwo = 19;

  /**
   * Mock objects.
   **/
  var result         = {  };
  var requestMockObj = {
    headers: {
      jwt: mockJwt,
      userid: mockGroupMemberOne,
      updateInfo: { }
    }, params: {
      groupId: mockGroupId
    }
  };
  var responseMockObj = {
    send: function (data) {
      result = data;
    }
  };

  it("should return the group information when sending the 'groupid' - getGroupMembers().", function (done) {
    setTimeout(function () {
      GroupController.getGroupMembers(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result).toContain(jasmine.objectContaining({
          userid: mockGroupMemberOne
        }));
        expect(result).toContain(jasmine.objectContaining({
          userid: mockGroupMemberTwo
        }));

        done();
      }, 600)
    }, 100);
  });

  it("should return 204 error if no one is found - getGroupInfo().", function (done) {
    requestMockObj.params.groupId = -1; // Should never exist.

    setTimeout(function () {
      GroupController.getGroupInfo(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(204); // No Content.
        done();
      }, 400);
    }, 100);
  });

  it("should return a 500 error if an integer is not supplied for 'groupid' - getGroup().", function (done) {
    requestMockObj.params.groupId = "fail";

    setTimeout(function () {
      GroupController.getGroupInfo(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(500); // Internal Server Error.
        done();
      }, 400);
    }, 100);
  });

  it("should update the group info successfully", function (done) {
    requestMockObj.headers.groupname = "other";
    requestMockObj.headers.groupdesc = "group";
    requestMockObj.params.groupId    = 2;

    setTimeout(function () {
      GroupController.updateGroupInfo(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result).toBe(true);
        done();
      }, 200);
    }, 100);
  });

  it("should return true if a group was deleted successfully - deleteGroup()", function (done) {
    requestMockObj.params.groupId = 3;

    setTimeout(function () {
      GroupController.deleteGroup(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result).toBe(true);
        done();
      }, 100);
    }, 100);
  });
});