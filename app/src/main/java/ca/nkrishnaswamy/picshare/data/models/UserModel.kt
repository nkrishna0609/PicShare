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
    @ColumnInfo(name="user_email")
    private var email: String,
    @ColumnInfo(name="user_username")
    private var username: String,
    @ColumnInfo(name="user_name")
    private var name: String,
    @ColumnInfo(name="user_profile_pic_path_from_uri")
    private var profilePicPathFromUri: String,
    @ColumnInfo(name="user_type_of_Profile_Pic")
    private var typeOfProfilePic : Int
    ): Parcelable {

    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0

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

    fun getTypeOfProfilePic() : Int {
        return typeOfProfilePic
    }

    fun setTypeOfProfilePic(type : Int) {
        typeOfProfilePic = type
    }

}