'use strict';

/**
 * Module under test.
 **/
var LocationDA = require('../../data_accessors/LocationDataAccessor');

describe("Unit test for Location Data Accessor", function () {
  /**
   * Mock objects.
   **/
  var result          = {};
  var paramsMockObj = {
    userId: ""
  };
  var sendMockObj = function (data) {
    result = data;
  };

  /**
   * Run before every test.
   **/
  beforeEach(function () {
    paramsMockObj.userId = "";

    result = { };
  });

  it("should return a 204 error if the user is not found.", function (done) {
    paramsMockObj.userId = -1; // Can never exist.

    setTimeout(function () {
      LocationDA.getUserLocation(paramsMockObj, sendMockObj);

      setTimeout(function () {
        expect(result.statusCode).toBe(204); // No Content.
        done();
      }, 375);
    }, 100);
  });
});