'use strict';

var express = require('express');
var router  = express.Router();

/**
 * Modules.
 **/
var UserController       = require('../controllers/UserController');
var MiddlewareController = require('../controllers/MiddlewareController');

/* Middleware. */
router.use(MiddlewareController.checkToken);
router.use(MiddlewareController.updateIP); 

/* GET current user information. */
router.get('/me', UserController.getCurrentUser);

/* POST update user information. */
router.post('/update', UserController.updateUser);

/* GET user's friends. */
router.get('/friends', UserController.getFriends);

/* POST update user's friends. */
router.post('/friends/update', UserController.updateFriends);

/* GET search user by name. */
router.get('/find', UserController.findUser);

module.exports = router;