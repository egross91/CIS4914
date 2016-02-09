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
   var testJWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lRmlyc3QiOiJ0ZXN0QHRlc3QuY29tIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNDU1MDQ5Njg4LCJleHAiOjE0NTUwNTY4ODh9.rnHix0ao4dDwaf6qaf8Cv67NBXNYlOhHd7oRRG-YrDc";

	/**
	 * Object mocks.
	 **/
  // The 'request.'
	var requestMockObj = {
		headers: {
      jwt: ""
    }
	};

  // The 'result' of requests.
  var result = { };

  // The 'response.'
  var responseMockObj = { 
    send: function (err, data) {
      result = err;
    },
    onSuccess: function () {
      result.success = true;
    }
  };

  beforeEach(function () {
    // Reset mock objects.
    requestMockObj.headers.jwt = "";

    result = {
      success: false
    };
  });

	it("should send a 400 error if no JWT is supplied", function () {
    MiddlewareController.checkToken(requestMockObj, responseMockObj);

    expect(result.statusCode).toBe(400);
	});

  it("should send a 407 error if the jwt is malformed", function (done) {
    // Setup.
    requestMockObj.headers.jwt = sendMockStr;

    // Force the assertion to wait sometime to catch up with the async functionality.
    setTimeout(function () {
      MiddlewareController.checkToken(requestMockObj, responseMockObj);
      setTimeout(function () {
        // Check the value now that the async method is done.
        expect(result.statusCode).toBe(407);
        done();
      }, 100);
    }, 100);
  });

  it("should call next() if successful", function (done) {
    requestMockObj.headers.jwt = testJWT;

    setTimeout(function () {
      MiddlewareController.checkToken(requestMockObj, responseMockObj, responseMockObj.onSuccess);
      setTimeout(function () {
        expect(result.success).toBe(true);
        done();
      }, 100);
    }, 100);
  });
}); 