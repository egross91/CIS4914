'use strict';

/**
 * Module under test.
 **/
var LocationController = require('../../controllers/LocationController');

describe("Unit test for Location Controller", function () {
  /**
   * Mock objects.
   **/
  var result          = {};
  var requestMockObj  = {
    params: {
      userId: ""
    }
  };
  var responseMockObj = {
    send: function (data) {
      result = data;
    }
  };

  /**
   * Run before every test.
   **/
  beforeEach(function () {
    requestMockObj.params.userId = "";

    result = { };
  });

  it("should return a 204 error if the user is not found.", function (done) {
    requestMockObj.params.userId = -1; // Can never exist.

    setTimeout(function () {
      LocationController.getUserLocation(requestMockObj, responseMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(204); // No Content.
        done();
      }, 600);
    }, 100);
  });
});