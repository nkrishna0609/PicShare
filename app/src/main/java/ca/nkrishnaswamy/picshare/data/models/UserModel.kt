package ca.nkrishnaswamy.picshare.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(private var email: String, private var username: String, private var name: String, private var profilePicPathFromUri: String): Parcelable {

    fun getEmail():String {
        return email
    }

    fun setUsername(enteredUsername: String){
        username=enteredUsername
    }

    fun getUsername():String {
        return username
    }

    fun setName(enteredName: String){
        name=enteredName
    }

    fun getName():String {
        return name
    }

    fun getProfilePicPath(): String {
        return profilePicPathFromUri
    }

    fun setProfilePic(imgPath: String) {
        profilePicPathFromUri = imgPath
    }

}