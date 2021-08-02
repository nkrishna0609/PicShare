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
                getPostsFromServer(context, idToken, email)
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

    suspend fun addPost(context: Context, post : UserPost, idToken: String) : Boolean {
        accountDao.insertPost(post)

        val postsList : List<UserPost> = accountDao.getUserModelWithUserPostsNonLiveData()[0].userPosts
        var newIdAfterInsertion : Long = -1L

        for (p in postsList) {
            if (post.caption == p.caption && post.email == p.email && post.uriImgPathString == p.uriImgPathString){
                newIdAfterInsertion = p.id
            }
        }

        if (newIdAfterInsertion == -1L) {
            post.id = newIdAfterInsertion
            accountDao.deletePost(post)
            return false
        }

        var checkSuccess = false
        val encodedBase64 : String = getBase64EncodedFromUri(context, post.uriImgPathString)

        val jsonObject = JSONObject()
        jsonObject.put("id", newIdAfterInsertion)
        jsonObject.put("caption", post.caption)
        jsonObject.put("postPicBase64", encodedBase64)
        jsonObject.put("firebaseUid", "")

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.createPost(requestBody, idToken)

        if (response.isSuccessful) {
            checkSuccess = true
        } else {
            post.id = newIdAfterInsertion
            accountDao.deletePost(post)
        }
        return checkSuccess
    }

    fun getPosts() : LiveData<List<SignedInAccountWithUserPosts>> {
        return livedataPostList
    }

    private suspend fun getPostsFromServer(context : Context, idToken : String, email : String) : Boolean {
        var checkSuccess = false
        val response = service.getPostsOfUser(idToken)

        if (response.isSuccessful) {
            val postList = response.body()
            if (postList != null) {
                for (i in 0 until postList.count()) {
                    val id = postList[i].id
                    val caption = postList[i].caption
                    val postPicBase64 = postList[i].postPicBase64

                    val uriImgPathString : String = getUriPathStringFromBase64(context, postPicBase64)

                    val postToInsert = UserPost(id, caption, uriImgPathString, email)
                    accountDao.insertPost(postToInsert)
                }
            }
            checkSuccess = true
        }

        return checkSuccess
    }

    suspend fun deletePost(post : UserPost, idToken: String) : Boolean {
        var checkSuccess = false
        val response = service.deletePost(idToken, post.id)
        if (response.isSuccessful) {
            checkSuccess = true
            accountDao.deletePost(post)
        }
        return checkSuccess
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

    suspend fun searchForAccounts(context: Context, searchQuery: String) : ArrayList<UserModel> {
        val searchedUsers = arrayListOf<UserModel>()

        val response = service.searchForUsers(searchQuery)

        if (response.isSuccessful) {
            val postList = response.body()
            if (postList != null) {
                for (i in 0 until postList.count()) {
                    val email : String = postList[i].email
                    val username : String = postList[i].username
                    val name : String = postList[i].name
                    val profilePicBase64 : String = postList[i].profilePicBase64
                    val bio : String = postList[i].bio
                    val followerNum : Int = postList[i].followerNum
                    val followingNum : Int = postList[i].followingNum

                    val profilePicUriPathString : String = getUriPathStringFromBase64(context, profilePicBase64)

                    val account = UserModel(0, email, username, name, profilePicUriPathString, bio, followerNum, followingNum)
                    searchedUsers.add(account)
                }
            }
        }

        return searchedUsers
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

        val file = File(context.cacheDir, "tempCachePic" + getRandomString(20))
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

    private fun getRandomString(length : Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { charset.random() }.joinToString("")
    }
}