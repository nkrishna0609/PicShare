'use strict';

var mongoose = require('mongoose');

// Create the User schema

var userSchema = new mongoose.Schema({
    email: String,
    username: String,
    name: String,
    profilePicBase64: String,
    bio: String,
    firebaseUid: String
});

// Build model
var model = mongoose.model('UserModel', userSchema);

// Export model
module.exports = model;