package ca.nkrishnaswamy.picshare.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.models.JSONUserModel
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.data.models.relations.SignedInAccountWithUserPosts
import ca.nkrishnaswamy.picshare.network.APIService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

class SignedInUserAccountRepository(private val accountDao: UserAccountDAO) {
    private var livedataUser : LiveData<UserModel> = accountDao.retrieveCurrentLoggedInUser()
    private var livedataPostList : LiveData<List<SignedInAccountWithUserPosts>> = accountDao.getUserModelWithUserPosts()
    private val service = APIService()

    fun getCurrentLoggedInUser() : LiveData<UserModel> {
        return livedataUser
    }

    suspend fun logInUser(context: Context, idToken: String) : Boolean {
        var successCheck = false
        val response = service.getUser(idToken)
        if (response.isSuccessful){
            val retrievedUser : JSONUserModel? = response.body()
            if (retrievedUser != null) {
                val email : String = retrievedUser.email
                val username : String = retrievedUser.username
                val name : String = retrievedUser.name
                val profilePicBase64 : String = retrievedUser.profilePicBase64
                val bio : String = retrievedUser.bio
                val followerNum : Int = retrievedUser.followerNum
                val followingNum : Int = retrievedUser.followingNum

                val profilePicUriPathString : String = getUriPathStringFromBase64(context, profilePicBase64)

                val account = UserModel(0, email, username, name, profilePicUriPathString, bio, followerNum, followingNum)
                accountDao.insertUser(account)
                successCheck = true
            }
        }
        return successCheck
    }

    suspend fun registerUser(context: Context, account: UserModel, idToken: String) : Boolean {
        var checkSuccess = false
        val encodedBase64 : String = getBase64EncodedFromUri(context, account.profilePicPathFromUri)

        val jsonObject = JSONObject()
        jsonObject.put("email", account.email)
        jsonObject.put("username", account.username)
        jsonObject.put("name", account.name)
        jsonObject.put("profilePicBase64", encodedBase64)
        jsonObject.put("bio", account.bio)
        jsonObject.put("followerNum", account.followerNum)
        jsonObject.put("followingNum", account.followingNum)
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

    suspend fun updateUser(context: Context, account: UserModel, idToken: String) : Boolean {
        var checkSuccess = false
        val encodedBase64 : String = getBase64EncodedFromUri(context, account.profilePicPathFromUri)

        val jsonObject = JSONObject()
        jsonObject.put("email", account.email)
        jsonObject.put("username", account.username)
        jsonObject.put("name", account.name)
        jsonObject.put("profilePicBase64", encodedBase64)
        jsonObject.put("bio", account.bio)
        jsonObject.put("followerNum", account.followerNum)
        jsonObject.put("followingNum", account.followingNum)
        jsonObject.put("firebaseUid", "")

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.updateUser(requestBody, idToken)

        if (response.isSuccessful) {
            accountDao.updateUser(account)
            checkSuccess = true
        }
        return checkSuccess
    }

    suspend fun checkIfUsernameIsAvailable(username: String) : Int {
        val response = service.checkIfUsernameExists(username)
        return if (response.isSuccessful) {
            0
        }else{
            if (response.code() == 400) {
                1
            } else{
                2
            }
        }
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

    suspend fun deleteAccountFromServer(idToken: String) : Boolean{
        var checkSuccess = false
        val response = service.deleteUser(idToken)
        if (response.isSuccessful) {
            checkSuccess = true
        }
        return checkSuccess
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

    private fun getUriPathStringFromBase64(context : Context, base64Encoded: String) : String {
        val imageAsByteArray : ByteArray = Base64.decode(base64Encoded, Base64.NO_WRAP)
        val bitmapProfilePic = BitmapFactory.decodeByteArray(imageAsByteArray, 0 ,imageAsByteArray.size)

        val file = File(context.cacheDir, "tempCacheProfilePic")
        file.delete()
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmapProfilePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()

        val uriImg = file.toURI()
        return uriImg.toString()
    }
}