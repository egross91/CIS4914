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

/* GET user's friends. */
router.get('/friends', UserController.getFriends);

/* GET user's groups. */
router.get('/groups', UserController.getGroups);

/* GET user group information. */
router.get('/group', UserController.getGroup);

/* POST update group information. */
// router.post('/group/update/:groupId', UserController.updateGroup);

module.exports = router;