var Postgres = require('pg');
var JWT      = require('jsonwebtoken');

var connectionString = process.env.MU_CONN_STR;
var jwtSecret        = process.env.MU_JWT_SECRET;

/**
 * Module under test.
 **/
var AuthDA = require('../../data_accessors/AuthDataAccessor');

describe("Testing the Authorization Data Accessor - makes DB calls.", function () {
  /**
   * Object mocks.
   **/  
  var requestMockObj = {
    email: "",
    password: ""
  };

  /* Clean up before each test. */
  beforeEach(function (done) {
    Postgres.connect(connectionString, function (err, client, disconnect) {
      client.query("DELETE FROM User_Pers;");
      client.query("DELETE FROM User_Gen;", function () {
        disconnect();
        done();
      });
    });

    requestMockObj.email    = "test@test.net";
    requestMockObj.password = "Test1234";
  });

  it("should register a user without error", function (done) {
    AuthDA.register(requestMockObj, function (err, data) {
      var result = JWT.decode(data, jwtSecret);

      expect(result).toBeTruthy();
      done();
    });
  });

  it("should not allow the same user to register twice", function (done) {
    AuthDA.register(requestMockObj, function (err, data) {
      expect(data).toBeTruthy();
    });

    AuthDA.register(requestMockObj, function (err, data) {
      expect(err.statusCode).toBe(409); // User exists.
      done();
    })
  });

  it("should NOT allow a user to login without registering", function (done) {
    AuthDA.login(requestMockObj, function (err, data) {
      expect(err.statusCode).toBe(400); // Bad Request.
      done();
    });
  })

  it("should allow a user to login AFTER registering", function (done) {
    AuthDA.register(requestMockObj, function (err, data) {
      expect(err.statusCode).toBe(200); // OK

      AuthDA.login(requestMockObj, function (err, data) {
        var token = JWT.decode(data, jwtSecret);

        expect(err.statusCode).toBe(200); // OK
        expect(data).toBeTruthy();
        expect(token).toBeTruthy();
        done();
      });
    });
  });

  it("should NOT login a user when they input the wrong password", function (done) {
    AuthDA.register(requestMockObj, function (err, data) {
      expect(err.statusCode).toBe(200); // OK

      requestMockObj.password = "incorrect";
      AuthDA.login(requestMockObj, function (err, data) {
        var token = JWT.decode(data, jwtSecret);

        expect(err.statusCode).toBe(401); // Unauthorized.
        expect(token).toBeFalsy();
        done();
      })
    })
  });
});