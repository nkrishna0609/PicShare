package ca.nkrishnaswamy.picshare.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.db.roomDbs.CurrentLoggedInUserCache
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.repositories.SignedInUserAccountRepository

class SignedInUserViewModel(application: Application) : AndroidViewModel(application) {

    private val userAccountDao : UserAccountDAO = CurrentLoggedInUserCache.getInstance(application).userAccountDAO()
    private val repository = SignedInUserAccountRepository(userAccountDao)
    private var livedataUser : LiveData<UserModel> = repository.getCurrentLoggedInUser()

    fun getCurrentLoggedInUser(): LiveData<UserModel> {
        return livedataUser
    }

    fun updateUser(account: UserModel) {
        repository.updateUser(account)
    }

    fun logInUser(account: UserModel) {
        repository.logInUser(account)
    }

    fun signOut() {
        repository.signOut()
    }

}