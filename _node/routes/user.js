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

/* POST update user information. */
router.post('/update', UserController.updateUser);

/* GET user's friends. */
router.get('/friends', UserController.getFriends);

/* POST update user's friends. */
router.post('/friends/update', UserController.updateFriends);

/* GET search user by name. */
router.get('/find', UserController.findUser);

/* GET user's groups. */
router.get('/groups', UserController.getGroups);

/* GET user group information. */
router.get('/group/:groupId', UserController.getGroup);

/* POST update group informatio */
router.post('/group/info/update/:groupId', UserController.updateGroupInfo);

/* POST update group members. */
router.post('/group/members/update/:groupId', UserController.updateGroupMembers);

module.exports = router;