'use strict';

var express = require('express');
var router = express.Router();

var AuthController = require('../controllers/AuthController');

// TODO: Middleware - JWT

/* POST register user. */
router.post('/register', AuthController.register);


/* POST login user. */
router.post('/login', AuthController.login);

module.exports = router;
