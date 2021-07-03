package ca.nkrishnaswamy.picshare.data.db.DAOs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ca.nkrishnaswamy.picshare.data.models.UserModel

@Dao
interface UserAccountDAO {

    @Query("SELECT * FROM signedInAccount LIMIT 1")
    fun retrieveCurrentLoggedInUser() : LiveData<UserModel>

    @Insert
    fun logInUser(currentUser: UserModel)

    @Delete
    fun signOutCurrentUser(currentUser: UserModel)

}