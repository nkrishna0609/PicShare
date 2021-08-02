package ca.nkrishnaswamy.picshare.data.models.roomModels

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName="userPost")
data class UserPost(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name="post_caption")
    var caption: String,
    @ColumnInfo(name="post_uri_img_path_string")
    var uriImgPathString: String,
    @ColumnInfo(name="email")
    var email: String     //identifier of user who has this post
): Parcelable