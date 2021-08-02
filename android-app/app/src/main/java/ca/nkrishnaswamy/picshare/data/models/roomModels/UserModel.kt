package ca.nkrishnaswamy.picshare.data.models.roomModels

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
    var id: Long = 0,
    @ColumnInfo(name="user_email")
    var email: String,
    @ColumnInfo(name="user_username")
    var username: String,
    @ColumnInfo(name="user_name")
    var name: String,
    @ColumnInfo(name="user_profile_pic_path_from_uri")
    var profilePicPathFromUri: String,
    @ColumnInfo(name="user_bio")
    var bio : String
    ): Parcelable