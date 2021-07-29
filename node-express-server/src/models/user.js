'use strict';

var mongoose = require('mongoose');

// Create the User schema

var userSchema = new mongoose.Schema({
    email: String,
    username: String,
    name: String,
    profilePicBase64: String,
    bio: String,
    followerNum: Number,
    followingNum: Number,
    firebaseUid: String
});

// Build model
var model = mongoose.model('User', userSchema);

// Export model
module.exports = model;