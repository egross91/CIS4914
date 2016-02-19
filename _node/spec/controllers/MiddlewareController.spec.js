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
   var expiredTestJWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lRmlyc3QiOiJ0ZXN0QHRlc3QuY29tIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNDU1MDQ5Njg4LCJleHAiOjE0NTUwNTY4ODh9.rnHix0ao4dDwaf6qaf8Cv67NBXNYlOhHd7oRRG-YrDc";
   var expiredJWTStr = "jwt expired";

   var testJWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lRmlyc3QiOiJ0ZXN0QHRlc3QuY29tIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNDU1MjI2NjE2fQ.gAr7tEh_d3sq4NmsSbo_rlW1cdIchCkQFq00VU4gNvI";

	/**
	 * Object mocks.
	 **/
  // The 'request.'
	var requestMockObj = {
		headers: {
      jwt: "",
      ip: ""
    }
	};

  // The 'result' of requests.
  var result = { };

  // The 'response.'
  var responseMockObj = { 
    send: function (data) {
      result = data;
    },
    onSuccess: function () {
      result.success = true;
    }
  };

  beforeEach(function () {
    // Reset mock objects.
    requestMockObj.headers.jwt = "";
    requestMockObj.headers.ip  = "";

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
    // Setup.
    requestMockObj.headers.jwt = testJWT;

    setTimeout(function () {
      MiddlewareController.checkToken(requestMockObj, responseMockObj, responseMockObj.onSuccess);
      setTimeout(function () {
        expect(result.success).toBe(true);
        done();
      }, 100);
    }, 100);
  });

  it("should return 407 error if JWT is expired", function (done) {
    // Setup.
    requestMockObj.headers.jwt = expiredTestJWT;

    setTimeout(function () {
      MiddlewareController.checkToken(requestMockObj, responseMockObj);
      setTimeout(function () {
        expect(result.statusCode).toBe(407);
        expect(result.messages[0]).toBe(expiredJWTStr);
        done();
      }, 100);
    }, 100);
  });

  it("should return a 412 error if no IP is supplied to updateIP()", function () {
    expect(requestMockObj.headers.ip).toBeFalsy();

    MiddlewareController.updateIP(requestMockObj, responseMockObj);
    expect(result.statusCode).toBe(412); // Precondition Failed.
  });
}); 