package ca.nkrishnaswamy.picshare.data.models.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost

data class SignedInAccountWithUserPosts(
    @Embedded val userModel: UserModel,
    @Relation(
        parentColumn = "user_email",
        entityColumn = "email"
    )
    val userPosts: List<UserPost>
)