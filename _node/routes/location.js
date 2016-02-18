'use strict';

var express = require('express');
var router = express.Router();

var LocationController = require('../controllers/LocationController');
var MiddlewareController = require('../controllers/MiddlewareController');

router.use(MiddlewareController.checkToken);
router.use(MiddlewareController.updateIP);

/* GET user location by id. */
router.get('/:userId', LocationController.getUserLocation);


module.exports = router;