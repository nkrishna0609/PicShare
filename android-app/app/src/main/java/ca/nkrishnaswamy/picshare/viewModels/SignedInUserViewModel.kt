package ca.nkrishnaswamy.picshare.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.db.roomDbs.CurrentLoggedInUserCache
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.data.models.relations.SignedInAccountWithUserPosts
import ca.nkrishnaswamy.picshare.repositories.SignedInUserAccountRepository

class SignedInUserViewModel(application: Application) : AndroidViewModel(application) {

    private val userAccountDao : UserAccountDAO = CurrentLoggedInUserCache.getInstance(application).userAccountDAO()
    private val repository = SignedInUserAccountRepository(userAccountDao)
    private var livedataUser : LiveData<UserModel> = repository.getCurrentLoggedInUser()
    private var livedataPostList : LiveData<List<SignedInAccountWithUserPosts>> = repository.getPosts()

    fun getCurrentLoggedInUser(): LiveData<UserModel> {
        return livedataUser
    }

    fun updateUser(account: UserModel) {
        repository.updateUser(account)
    }

    fun logInUser(firebaseIdToken: String) {
        repository.logInUser(firebaseIdToken)
    }

    fun registerUser(account: UserModel) {
        repository.registerUser(account)
    }

    //fun signOut(context : Context) {
        //repository.signOut(context)
    //}

    fun deleteUser(context : Context) {
        deleteAllPosts()
        repository.deleteAccount(context)
    }

    fun addPost(post : UserPost) {
        repository.addPost(post)
    }

    fun getPosts(): LiveData<List<SignedInAccountWithUserPosts>> {
        return livedataPostList
    }

    fun deletePost(post: UserPost) {
        repository.deletePost(post)
    }

    private fun deleteAllPosts() {
        repository.deleteAllPosts()
    }
}