package ca.nkrishnaswamy.picshare.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.data.models.relations.SignedInAccountWithUserPosts
import ca.nkrishnaswamy.picshare.network.APIService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class SignedInUserAccountRepository(private val accountDao: UserAccountDAO) {
    private var livedataUser : LiveData<UserModel> = accountDao.retrieveCurrentLoggedInUser()
    private var livedataPostList : LiveData<List<SignedInAccountWithUserPosts>> = accountDao.getUserModelWithUserPosts()
    private val service = APIService()

    fun getCurrentLoggedInUser() : LiveData<UserModel> {
        return livedataUser
    }

    fun logInUser(firebaseIdToken: String) {
        //TODO: get user from Node server with firebaseIdToken and pass that user into accountDao.insertUser()
        //accountDao.insertUser(account)
    }

    suspend fun registerUser(context: Context, account: UserModel, idToken: String) : Boolean {
        var checkSuccess = false
        val encodedBase64 : String = getBase64EncodedFromUri(context, account.getProfilePicPathFromUri())

        val jsonObject = JSONObject()
        jsonObject.put("email", account.getEmail())
        jsonObject.put("username", account.getUsername())
        jsonObject.put("name", account.getName())
        jsonObject.put("profilePicBase64", encodedBase64)
        jsonObject.put("bio", account.getBio())
        jsonObject.put("followerNum", account.getFollowerNum())
        jsonObject.put("followingNum", account.getFollowingNum())
        jsonObject.put("firebaseUid", "")

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.createUser(requestBody, idToken)

        if (response.isSuccessful) {
            accountDao.insertUser(account)
            checkSuccess = true
        }
        return checkSuccess
    }

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

    fun deleteAccountFromCache(context : Context) {
        context.cacheDir.deleteRecursively()
        accountDao.deleteAccount()
    }

    private fun getBase64EncodedFromUri(context: Context, uriString: String): String {
        val uriImg: Uri = Uri.parse(uriString)
        val bitmap: Bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uriImg))
        val byteArrOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
        val byteArr: ByteArray = byteArrOutputStream.toByteArray()
        return Base64.encodeToString(byteArr, Base64.NO_WRAP)
    }

}