package ca.nkrishnaswamy.picshare.data.models

import com.google.gson.annotations.SerializedName

data class JSONUserModel(
    @SerializedName("email")
    var email: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("profilePicBase64")
    var profilePicBase64: String,
    @SerializedName("bio")
    var bio : String,
    @SerializedName("followerNum")
    var followerNum : Int,
    @SerializedName("followingNum")
    var followingNum : Int,
    @SerializedName("firebaseUid")
    var firebaseUid : String
)
