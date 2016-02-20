'use strict';

var express = require('express');
var router  = express.Router();

/**
 * Modules.
 **/
var UserController = require('../controllers/UserController');

/* GET user's friends. */
router.get('/friends', UserController.getFriends);

module.exports = router;