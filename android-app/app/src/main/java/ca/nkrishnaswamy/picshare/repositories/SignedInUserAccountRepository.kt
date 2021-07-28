package ca.nkrishnaswamy.picshare.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.data.models.relations.SignedInAccountWithUserPosts

class SignedInUserAccountRepository(private val accountDao: UserAccountDAO) {
    private var livedataUser : LiveData<UserModel> = accountDao.retrieveCurrentLoggedInUser()
    private var livedataPostList : LiveData<List<SignedInAccountWithUserPosts>> = accountDao.getUserModelWithUserPosts()

    fun getCurrentLoggedInUser() : LiveData<UserModel> {
        return livedataUser
    }

    fun logInUser(firebaseIdToken: String) {
        //TODO: get user from Node server with firebaseIdToken and pass that user into accountDao.insertUser()
        //accountDao.insertUser(account)
    }

    fun registerUser(account: UserModel) {
        accountDao.insertUser(account)
    }

    //fun signOut(context : Context) {
        //accountDao.signOut()
    //}

    fun updateUser(account: UserModel) {
        accountDao.updateUser(account)
    }

    fun addPost(post : UserPost) {
        accountDao.insertPost(post)
    }

    fun getPosts() : LiveData<List<SignedInAccountWithUserPosts>> {
        return livedataPostList
    }

    fun deletePost(post : UserPost) {
        accountDao.deletePost(post)
    }

    fun deleteAllPosts() {
        accountDao.deleteAllPosts()
    }

    fun deleteAccount(context : Context) {
        context.cacheDir.deleteRecursively()
        accountDao.deleteAccount()
    }

}