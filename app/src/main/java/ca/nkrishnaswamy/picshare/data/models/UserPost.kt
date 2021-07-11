package ca.nkrishnaswamy.picshare.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName="userPost")
data class UserPost(
    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0,
    @ColumnInfo(name="post_caption")
    private var caption: String,
    @ColumnInfo(name="post_uri_img_path_string")
    private var uriImgPathString: String,
    @ColumnInfo(name="email")
    private var email: String     //identifier of user who has this post
): Parcelable {

    fun getId(): Long{
        return id
    }

    fun setId(num: Long){
        id=num
    }

    fun getEmail(): String {
        return email
    }

    fun getCaption(): String {
        return caption
    }

    fun setCaption(enteredCaption : String) {
        caption = enteredCaption
    }

    fun getUriImgPathString(): String {
        return uriImgPathString
    }

    fun setUriImgPathString(imgStringPath: String) {
        uriImgPathString = imgStringPath
    }
}