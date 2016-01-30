/**
 * Module under test.
 **/
var ErrorHelper = require('../../helpers/ErrorHelper');

describe("Testing the ErrorHelper for better error message handling.", function () {
  /**
   * Object mocks.
   **/  
  var errorHandlerMockObj = {
    hasErrors: false,
    messages: [],
    statusCode: 200
  };

  var errMockObj = {
    messages: [],
    error: [],
    message: null
  };

  /**
   * Reusable variables.
   **/
  var errorMessage      = "Test error.";
  var throwErrorMessage = "No error handler.";


  beforeEach(function () {
    // Reset the Mock Obj back to default settings.
    errorHandlerMockObj.hasErrors  = false;
    errorHandlerMockObj.messages   = [];
    errorHandlerMockObj.statusCode = 200;

    errMockObj.messages = [];
    errMockObj.error    = [];
    errMockObj.message  = null;
  });

  it("should give a 500 error if the API is called without a status code - mergeMessages", function () {
    expect(errorHandlerMockObj.hasErrors).toBe(false);
    expect(errorHandlerMockObj.statusCode).toBe(200);

    ErrorHelper.mergeMessages(errorHandlerMockObj);
    expect(errorHandlerMockObj.hasErrors).toBe(true);
    expect(errorHandlerMockObj.statusCode).toBe(500);
  });

  it("should change the errorHandler's statusCode to what is passed", function () {
    expect(errorHandlerMockObj.hasErrors).toBe(false);
    expect(errorHandlerMockObj.statusCode).toBe(200);

    ErrorHelper.mergeMessages(errorHandlerMockObj, 400);
    expect(errorHandlerMockObj.hasErrors).toBe(true);
    expect(errorHandlerMockObj.statusCode).toBe(400);
  });

  it("should add messages to the errorHandler if an error is passed into it with a property of 'messages'", function () {
    /* Error setup. */
    errMockObj.messages.push(errorMessage);
    errMockObj.messages.push(errorMessage);
    expect(errorHandlerMockObj.messages.length).toBe(0); // No messages.

    ErrorHelper.mergeMessages(errorHandlerMockObj, 400, errMockObj);
    expect(errorHandlerMockObj.messages.length).toBe(2);
    expect(errorHandlerMockObj.messages[0]).toBe(errorMessage);
    expect(errorHandlerMockObj.messages[1]).toBe(errorMessage);
  });

  it("should add messages to the errorHandler if an error is pass into it with a property of 'error'", function () {
    /* Error setup. */
    errMockObj.error.push(errorMessage);
    errMockObj.error.push(errorMessage);
    expect(errorHandlerMockObj.messages.length).toBe(0); // No messages.

    ErrorHelper.mergeMessages(errorHandlerMockObj, 400, errMockObj);
    expect(errorHandlerMockObj.messages.length).toBe(2);
    expect(errorHandlerMockObj.messages[0]).toBe(errorMessage);
    expect(errorHandlerMockObj.messages[1]).toBe(errorMessage);
  });

  it("should add a message to the errorHandler if an error is pass into it with a property of 'message'", function () {
    /* Error setup. */
    errMockObj.message = errorMessage;
    expect(errorHandlerMockObj.messages.length).toBe(0); // No messages.

    ErrorHelper.mergeMessages(errorHandlerMockObj, 400, errMockObj);
    expect(errorHandlerMockObj.messages.length).toBe(1);
    expect(errorHandlerMockObj.messages[0]).toBe(errorMessage);
  });

  it 

  it("should give a 500 error if the API is called without a status code - addMessages", function () {
    expect(errorHandlerMockObj.statusCode).toBe(200);

    ErrorHelper.addMessages(errorHandlerMockObj);
    expect(errorHandlerMockObj.statusCode).toBe(500);
  });

  it("should throw an error if an object is not supplied as the handler", function () {
    try {
      ErrorHelper.mergeMessages();
    } catch (e) {
      expect(e).toBe(throwErrorMessage);
    }

    try {
      ErrorHelper.addMessages();
    } catch (e) {
      expect(e).toBe(throwErrorMessage);
    }

    try {
      ErrorHelper.mergeMessages(123);
    } catch (e) {
      expect(e).toBe(throwErrorMessage);
    }
  });

  it("should add messages that are supplied not through an object", function () {
    expect(errorHandlerMockObj.messages.length).toBe(0);

    ErrorHelper.addMessages(errorHandlerMockObj, 404, errorMessage, errorMessage);
    expect(errorHandlerMockObj.statusCode).toBe(404);
    expect(errorHandlerMockObj.messages.length).toBe(2);
    expect(errorHandlerMockObj.messages[0]).toBe(errorMessage);
    expect(errorHandlerMockObj.messages[1]).toBe(errorMessage);
  });

  it("should not add empty strings", function () {
    expect(errorHandlerMockObj.messages.length).toBe(0);

    ErrorHelper.addMessages(errorHandlerMockObj, 404, "", "", "");
    expect(errorHandlerMockObj.messages.length).toBe(0);
  });
});