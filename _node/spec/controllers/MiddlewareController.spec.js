'use strict';

/**
 * Module under test.
 **/
 var MiddlewareController = require('../../controllers/MiddlewareController');

describe("Unit test for Middleware Controller", function () {
  /**
   * Strings.
   **/
   var sendMockStr = "send mock";

	/**
	 * Object mocks.
	 **/
	var requestMockObj = {
		headers: {
      jwt: ""
    }
	};

  var responseMockObj = {
    result: {},
    send: function (err, data) {
      this.result = err;
    }
  };

  beforeEach(function () {
    // Reset mock objects.
    requestMockObj.headers.jwt = "";
    responseMockObj.result = {};
  });

	it("should send a 400 error if no JWT is supplied", function () {
    MiddlewareController.checkToken(requestMockObj, responseMockObj);

    expect(responseMockObj.result.statusCode).toBe(400);
	});

  // it("should send a 407 error if the jwt cannot be verified", function () {
  //   // Setup.
  //   requestMockObj.headers.jwt = "mock";

  //   MiddlewareController.checkToken(requestMockObj, responseMockObj);
  //   waitsFor(function () {}, 200);
  //   expect(responseMockObj.result.statusCode).toBe(407);
  // });
}); 