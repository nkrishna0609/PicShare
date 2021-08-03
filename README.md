# PicShare
![PicShare Banner](https://user-images.githubusercontent.com/56279640/127944739-1b7addaf-7d9f-48ce-9ba2-73bcdebd4e4d.png)

## TLDR
* An **Android** photo-sharing app written in **Kotlin**, with a **Node/Express** back end using **Firebase Authentication** with **MongoDB** as the database
* Allows for users to create, update, search for, and delete their accounts, while being able to create PicShare posts for photo sharing 
* Utilized the **MVVM** architecture for modular, readable and scalable code

## Overview
I was inspired by the Instagram mobile app, and created PicShare with my family circle in mind. PicShare allows for my close family and friends to display and share photos with each other by creating "posts". PicShare users can manage their accounts and search for other users on the app, and they can share their pictures by creating posts.

PicShare has 2 main components - the Android app, and the Node.js/Express.js server (with MongoDB as the database). The Android app was written entirely in Kotlin, and of course, the Node/Express server in JavaScript. 

## Features
### Registering Account
[![IMAGE ALT TEXT](http://img.youtube.com/vi/MXn0vvcxvIc/0.jpg)](https://www.youtube.com/watch?v=MXn0vvcxvIc "PicShare Demo - Registering Account")

### Login and Deletion of Account
[![IMAGE ALT TEXT](http://img.youtube.com/vi/fX1CL9WUEUc/0.jpg)](https://www.youtube.com/watch?v=fX1CL9WUEUc "PicShare Demo - Login and Deletion of Account")

### Editing Account
[![IMAGE ALT TEXT](http://img.youtube.com/vi/rrMuWxVIMCo/0.jpg)](https://www.youtube.com/watch?v=rrMuWxVIMCo "PicShare Demo - Editing Account")

### Persistence of Account
[![IMAGE ALT TEXT](http://img.youtube.com/vi/ujG2HoYCnJk/0.jpg)](https://www.youtube.com/watch?v=ujG2HoYCnJk "PicShare Demo - Persistence of User Account")

### Search for Users
[![IMAGE ALT TEXT](http://img.youtube.com/vi/SViiZ3IvGQI/0.jpg)](https://www.youtube.com/watch?v=SViiZ3IvGQI "PicShare Demo - Search Function")

### Resetting Password
[![IMAGE ALT TEXT](http://img.youtube.com/vi/jE3GccUIF5E/0.jpg)](https://www.youtube.com/watch?v=jE3GccUIF5E "PicShare Demo - Resetting Password")

### Creating and Viewing Posts
[![IMAGE ALT TEXT](http://img.youtube.com/vi/sWlbJbiSCbI/0.jpg)](https://www.youtube.com/watch?v=sWlbJbiSCbI "PicShare Demo - Creating and Viewing a Post")

### Deleting Posts
[![IMAGE ALT TEXT](http://img.youtube.com/vi/IBPTKv2_63M/0.jpg)](https://www.youtube.com/watch?v=IBPTKv2_63M "PicShare Demo - Deleting Posts")

## Technical Design Aspect
### Built with
* **Kotlin**
* **Node.js**
* **Express.js**
* **MongoDB**
* **Firebase Authentication**

### Brief description of app - server interaction
The Android app communicates with the server via HTTP methods - GET, POST, PUT and DELETE, by leveraging Retrofit (which uses the OkHttp library). The server handles all the incoming requests via Express routes. The app receives data in the form of JSON from the server, and utilizes Gson to convert to POJO. From there, the Android app uses the Room persistence library to cache (user account and post data). 

Firebase provides authentication and security measures with login/registering processes and HTTP requests. The Android app sends a Firebase ID token to the server with every HTTP request - the server uses the Firebase Admin SDK to verify and decode ID tokens to ensure validity. If an ID token is not valid, the server responds with the appropriate error.

### MVVM architecture
The Android app uses the MVVM architecture for modular, scalable, and maintainable code - by preventing tight coupling and "spaghetti" code. It ensures that the code has firm structure by making small classes with single responsibilities. 

As the name suggests, there are 3 components - the Models, the ViewModels, and the Views. 
The MVVM architecture ensures that:
* the Views do not contain business logic - they're solely present to handle the UI
* the ViewModel has direct reference to the Repository which is the single source of truth - the Repository has reference to the remote data source logic and the local database
* the ViewModel updates the View via LiveData - no need to manually update the View whenever the exposed data changes (the View observes the ViewModel)


