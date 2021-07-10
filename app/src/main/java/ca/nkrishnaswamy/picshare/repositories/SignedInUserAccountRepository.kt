package ca.nkrishnaswamy.picshare.repositories

import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.models.UserModel

class SignedInUserAccountRepository(private val accountDao: UserAccountDAO) {
    private var livedataUser : LiveData<UserModel> = accountDao.retrieveCurrentLoggedInUser()

    fun getCurrentLoggedInUser() : LiveData<UserModel> {
        return livedataUser
    }

    fun logInUser(account: UserModel) {
        accountDao.logInUser(account)
    }

    fun signOut() {
        accountDao.signOut()
    }

    fun updateUser(account: UserModel) {
        accountDao.updateUser(account)
    }

}