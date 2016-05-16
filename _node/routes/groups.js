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

/* GET user group information. */
router.get('/:groupId', GroupController.getGroup);

/* POST delete group. */
router.post('/delete/:groupId', GroupController.deleteGroup);

/* POST update group informatio */
router.post('/info/update/:groupId', GroupController.updateGroupInfo);

/* POST update group members. */
router.post('/members/update/:groupId', GroupController.updateGroupMembers);

module.exports = router;