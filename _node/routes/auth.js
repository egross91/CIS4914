'use strict';

var express = require('express');
var router = express.Router();

var AuthController = require('../controllers/AuthController');

/* POST register user. */
/* TODO: Registration. */

/* POST login user. */
router.post('/login', AuthController.login);

module.exports = router;
