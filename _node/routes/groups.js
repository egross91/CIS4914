'use strict';

var express = require('express');
var router  = express.Router();

/**
 * Modules.
 */
var GroupController      = require('../controllers/GroupController');
var MiddlewareController = require('../controllers/MiddlewareController');

/* Middleware. */
router.use(MiddlewareController.checkToken);
router.use(MiddlewareController.updateIP);

/* GET user's groups. */
router.get('/groups', GroupController.getGroups);

/* GET next possible group id. */
router.get('/groups/id', GroupController.getNextGroupId);

/* POST delete group. */
router.post('/delete/:groupId', GroupController.deleteGroup);

/* GET user group info. */
router.get('/info/:groupId', GroupController.getGroupInfo);

/* POST update group informatio */
router.post('/info/update/:groupId', GroupController.updateGroupInfo);

/* GET user group members. */
router.get('/members/:groupId', GroupController.getGroupMembers);

/* POST update group members. */
router.post('/members/update/:groupId', GroupController.updateGroupMembers);

module.exports = router;