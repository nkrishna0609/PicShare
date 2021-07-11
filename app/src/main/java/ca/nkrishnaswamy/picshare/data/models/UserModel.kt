package ca.nkrishnaswamy.picshare.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName="signedInAccount", indices = [Index(value=["user_email"], unique=true)])
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0,
    @ColumnInfo(name="user_email")
    private var email: String,
    @ColumnInfo(name="user_username")
    private var username: String,
    @ColumnInfo(name="user_name")
    private var name: String,
    @ColumnInfo(name="user_profile_pic_path_from_uri")
    private var profilePicPathFromUri: String,
    @ColumnInfo(name="user_bio")
    private var bio : String,
    @ColumnInfo(name="user_num_followers")
    private var followerNum : Int,
    @ColumnInfo(name="user_num_following")
    private var followingNum : Int,
    @ColumnInfo(name="user_num_posts")
    private var postsNum : Int
    ): Parcelable {

    fun getId(): Long{
        return id
    }

    fun setId(num: Long){
        id=num
    }

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

    fun getProfilePicPathFromUri(): String {
        return profilePicPathFromUri
    }

    fun setProfilePicPathFromUri(imgPath: String) {
        profilePicPathFromUri = imgPath
    }

    fun getBio() : String {
        return bio
    }

    fun setBio(enteredBio : String) {
        bio = enteredBio
    }

    fun getFollowerNum() : Int {
        return followerNum
    }

    fun setFollowerNum(followerCount : Int) {
        followerNum = followerCount
    }

    fun getFollowingNum() : Int {
        return followingNum
    }

    fun setFollowingNum(followingCount : Int) {
        followingNum = followingCount
    }

    fun getPostsNum() : Int {
        return postsNum
    }

    fun setPostsNum(postsCount : Int) {
        postsNum = postsCount
    }

}