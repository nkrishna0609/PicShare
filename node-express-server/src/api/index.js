'use strict';

// Import Express
var express = require('express');
var User = require('../models/user');
var Post = require('../models/post');

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

// GET - fetches user by Firebase ID Token
router.get('/users/:idToken', function(request, response){
    var idToken = request.params.idToken

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The user does not exist in the database."});
            }
            response.send(userFound);
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })

})

// GET - fetches all users which have usernames or names containing search query
router.get('/users/search/:searchQuery', function(request, response){
    var searchQuery = request.params.searchQuery;
    var regexSearchQuery = new RegExp(searchQuery, 'i');

    User.find().or( [{"name": {$regex: regexSearchQuery}}, {"username": {$regex: regexSearchQuery}}]).exec(function(err, users) {
        if (err) {
            return response.status(500).json({message: err.message});
        }

        response.send(users);

    });
});

// GET - get all posts of a user
router.get('/users/posts/:idToken', function(request, response){
    var idToken = request.params.idToken

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The user does not exist in the database."});
            }
            Post.find(query, function(err, posts){
                if (err) {
                    return response.status(500).json({message: err.message});
                }
        
                response.send(posts);
            });
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })
})

// GET - check if username exists already
router.get('/users/username/:username', function(request, response){
    var username = request.params.username

    User.findOne({"username": username}, function(err, userFound) {
        if (err) {
            return response.status(500).json({err, userFound});
        }

        if (userFound){
            return response.status(400).json({err: "The username already exists in the database."});
        }

        response.json({message: "This username is available."})
    });
});

// POST - adds user to the database
router.post('/users/:idToken', function(request, response){
    var user = request.body;
    var email = user.email;
    var query = {'email': email};
    var idToken = request.params.idToken
    var picBase64 = request.body.profilePicBase64

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
                user['profilePicBase64'] = picBase64

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

// POST - create a new post and add it to user's post collection via user's Firebase uid
router.post('/users/posts/:idToken', function(request, response){
    var post = request.body;
    var idToken = request.params.idToken;
    var postPicBase64 = request.body.postPicBase64

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid;
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The user does not exist in the database."});
            }
            
            post['firebaseUid'] = uid;
            post['postPicBase64'] = postPicBase64
            
            Post.create(post, function(err, post){
                if (err) {
                    return response.status(500).json({err, post});
                }
        
                response.json({'post': post, message: 'Post created.'});
            });
            
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })    
});

// PUT - updates an existing user in the database
router.put('/users/:idToken', function(request, response){
    var idToken = request.params.idToken;
    var user = request.body;
    var picBase64 = request.body.profilePicBase64

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid;
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The user does not exist in the database."});
            }
            
            user['firebaseUid'] = uid;
            user['profilePicBase64'] = picBase64
            
            User.findOneAndUpdate(query, user, {new: true}, (err, user) => {
                if(err) {
                  return response.status(500).json({err: err.message});
                }
            
                response.json({'user': user, message: 'User updated.'});
            });
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })
});

// DELETE - deletes existing user from database
router.delete('/users/:idToken', function(request, response){
    var idToken = request.params.idToken;

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The user does not exist in the database."});
            }

            Post.deleteMany(query).then(function(){
                // success of deleting all posts of a user
            }).catch(function(error){
                return response.status(500).json({err: err.message});
            });

            User.findOneAndDelete(query, function(err, user){
                if(err) {
                    return response.status(500).json({err: err.message});
                }
    
                response.json({message: 'User deleted.'});
            });
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })
});

// DELETE - deletes a post from database by postId
router.delete('/users/posts/:idToken/:postId', function(request, response){
    var idToken = request.params.idToken;
    var postId = request.params.postId;

    admin.auth().verifyIdToken(idToken).then((decodeToken) => {
        const uid = decodeToken.uid
        var query = {'firebaseUid': uid};
        
        User.findOne(query, function(err, userFound) {
            if (err) {
                return response.status(500).json({err, userFound});
            }
    
            if (!userFound){
                return response.status(500).json({err: "The post which is to be deleted does not belong to a user."});
            }
            Post.findOneAndDelete({'firebaseUid': uid, 'id':postId}, function(err, post){
                if(err) {
                    return response.status(500).json({err: err.message});
                }
    
                response.json({message: 'Post deleted.'});
            });
        });
    })
    .catch((error) => {
        return response.status(500).json({err: "Invalid Firebase ID token."})
    })
});

module.exports = router;