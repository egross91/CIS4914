'use strict';

exports.mergeMessages = function(to, statusCode, from) {
	var message  = "";

  if (argOk(to, "object")) {
    to.hasErrors = true;
    
  	if (argOk(from, "object")) {
      if (from.messages) {
    		for (var i = 0; i < from.messages.length; ++i) {
          message = from.messages[i];

    			if (argOk(message, "string"))
    				to.messages.push(message);
    		}
      } 
      if (from.error) {
        for (var i = 0; i < from.error.length; ++i) {
          message = from.error[i];

          if (argOk(message, "string")) {
            to.messages.push(message);
          }
        }
      }
      if (from.message) {
        to.messages.push(from.message);
      }
  	}
  } else {
    throw "No error handler.";
  }

  setStatusCode(to, statusCode);
};

exports.addMessages = function() {
	var to         = arguments[0];
  var statusCode = arguments[1];
	var arg;

	if (argOk(to, "object")) {
		to.hasErrors  = true;

		for (var i = 1; i < arguments.length; ++i) {
			arg = arguments[i];

			if (argOk(arg, "string"))
				to.messages.push(arg);
		}
	} else {
    throw "No error handler.";
  }

  setStatusCode(to, statusCode);
};

/**
 * Helper functions.
 **/
var argOk = function (arg, type) {
  return (arg && typeof arg === type);
};

var setStatusCode = function (handler, code) {
  if (argOk(code, "number")) {
    handler.statusCode = code;
  } else {
    handler.statusCode = 500;
  }
};