'use strict';

// Import Express
var express = require('express');
var User = require('../models/user');

// Import .env
const dotenv = require('dotenv');
dotenv.config();

// Import Firebase Admin Account
var admin = require("firebase-admin");

// Create a router
var router = express.Router();

// Configure Firebase Admin Account
if (!admin.apps.length){
    admin.initializeApp({
        credential: admin.credential.cert({
            projectId: process.env.FIREBASE_PROJECT_ID,
            clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
            privateKey: process.env.FIREBASE_PRIVATE_KEY
        })
    });
} else {
    admin.app();
}

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
router.post('/users/:idToken', function(request, response){
    var user = request.body;
    var email = user.email;
    var query = {'email': email};
    var idToken = request.params.idToken

    User.findOne(query, function(err, userNew) {
        if (err) {
            return response.status(500).json({err, userNew});
        }

        if (userNew){
            return response.status(500).json({err: "The user already exists in the database."});
        }

        else {

            admin.auth().verifyIdToken(idToken).then((decodeToken) => {
                const uid = decodeToken.uid

                user['firebaseUid'] = uid;

                User.create(user, function(err, user){
                    if (err) {
                        return response.status(500).json({err, user});
                    }
            
                    response.json({'user': user, message: 'User created.'});
                });
            })
            .catch((error) => {
                return response.status(500).json({err: "Invalid Firebase ID token."})
            })
        }
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

// DELETE - deletes existing user from database
router.delete('/users/:email', function(request, response){
    var email = request.params.email;
    var query = {'email': email};

    User.findOne(query, function(err, userNew) {
        if (err) {
            return response.status(500).json({err, userNew});
        }

        if (!userNew){
            return response.status(500).json({err: "Did not find a user with the email provided."});
        }

        else {
            User.findOneAndDelete(query, function(err, user){
                if(err) {
                    return response.status(500).json({err: err.message});
                }
    
                response.json({'user': user, message: 'User deleted.'});
            });
        }
    });
});

module.exports = router;