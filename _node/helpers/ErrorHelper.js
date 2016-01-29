'use strict';

exports.mergeMessages = function(to, statusCode, from) {
	var message   = "";
  to.hasErrors  = true;
  to.statusCode = from.statusCode;
  
	if (from.messages) {
		for (var i = 0; i < from.messages.length; ++i) {
      message = from.messages[i];

			if (messageOk(message))
				to.messages.push(message);
		}
	} else if (from.error) {
    for (var i = 0; i < from.error.length; ++i) {
      message = from.error[i];

      if (messageOk(message)) {
        to.messages.push(err);
      }
    }
  } else if (from.message) {
		to.messages.push(from.message);
	}

  setStatusCode(to, statusCode);
};

exports.addMessages = function() {
	var to         = arguments[0];
  var statusCode = arguments[1];
	var arg;

	if (typeof to === "object") {
		to.hasErrors  = true;

		for (var i = 1; i < arguments.length; ++i) {
			arg = arguments[i];

			if (messageOk(arg))
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
var messageOk = function (msg) {
  return (msg && typeof msg === "string");
};

var setStatusCode = function (handler, code) {
  if (code && typeof code === "number") {
    handler.statusCode = code;
  } else {
    handler.statusCode = 500;
  }
};