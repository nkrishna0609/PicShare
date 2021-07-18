'use strict';

var mongoose = require('mongoose');

// Create the Post schema

var postSchema = new mongoose.Schema({
    id: Number,
    caption: String,
    uriImgPathString: String,
    firebaseUid: String
});

// Build model
var model = mongoose.model('Post', postSchema);

// Export model
module.exports = model;