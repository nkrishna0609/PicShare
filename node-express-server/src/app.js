'use strict';

// Import statements
var express = require('express');
var router = require('./api');
require('./database');

// Configure Express
var app = express();
app.use(express.json());    //for parsing json since bodyparser is depreciated
app.use('/api',router);

// Configure server to listen on port 3000
app.listen(3000, function(){
    console.log("The server is running on port 3000");
});