'use strict';

var express = require('express');
var router = express.Router();

var LocationController = require('../controllers/LocationController');
var MiddlewareController = require('../controllers/MiddlewareController');

// router.use(MiddlewareController.checkToken);

// TODO: LocationController.
router.get('/:userId', LocationController.getUserLocation);


module.exports = router;