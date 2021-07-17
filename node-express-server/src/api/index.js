'use strict';

// Import Express
var express = require('express');
var User = require('../models/user');

// Create a router
var router = express.Router();

// API ROUTES

// GET - fetches ALL users
router.get('/users', function(request, response){
    User.find({}, function(err, users) {
        if (err) {
            return response.status(500).json({message: err.message});
        }

        response.json({users: users});

    });
});

// POST - adds user to the database
router.post('/users', function(request, response){
    var user = request.body;
    User.create(user, function(err, user){
        if (err) {
            return response.status(500).json({err, user});
        }

        response.json({'user': user, message: 'User Created'});

    });
});

// PUT - updates an existing user in the database
router.put('/users/:email', function(request, response){
    var email = request.params.email;
    var user = request.body;

    if(user && user.email != email) {
        return response.status(500).json({err: "Did not find a user with the email provided."});
      }

      var query = {'email': email};
    
      User.findOneAndUpdate(query, user, {new: true}, (err, user) => {
        if(err) {
          return response.status(500).json({err: err.message});
        }
    
        response.json({'user': user, message: 'User updated.'});
    });
});


module.exports = router;