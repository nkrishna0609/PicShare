package ca.nkrishnaswamy.picshare.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (private var email: String, private var username: String, private var name: String): Parcelable {

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

}