'use strict';

exports.mergeMessages = function(to, from) {
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
};

exports.addMessages = function() {
	var to = arguments[0];
	var arg;

	if (typeof to === "object") {
		to.hasErrors = true;

		for (var i = 1; i < arguments.length; ++i) {
			arg = arguments[i];

			if (messageOk(arg))
				to.messages.push(arg);
		}
	}
};

/**
 * Helper functions.
 **/
var messageOk = function(msg) {
  return (msg && typeof msg === "string");
};