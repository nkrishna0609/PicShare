package ca.nkrishnaswamy.picshare.network

import ca.nkrishnaswamy.picshare.BuildConfig
import ca.nkrishnaswamy.picshare.data.models.JSONPostModel
import ca.nkrishnaswamy.picshare.data.models.JSONUserModel
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val baseUrl = BuildConfig.BASE_URL

interface APIService {

    companion object {
        operator fun invoke(): APIService {
            val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit.create(APIService::class.java)
        }
    }

    @GET("/api/users/{idToken}")
    suspend fun getUser(@Path("idToken") idToken: String): Response<JSONUserModel>

    @GET("/api/users/username/{username}")
    suspend fun checkIfUsernameExistsRegister(@Path("username") username: String): Response<ResponseBody>

    @GET("/api/users/username/{username}/{email}")
    suspend fun checkIfUsernameExistsEditProfile(@Path("username") username: String, @Path("email") email: String): Response<ResponseBody>

    @GET("/api/users/posts/{idToken}")
    suspend fun getPostsOfLoggingInUser(@Path("idToken") idToken: String): Response<List<JSONPostModel>>

    @GET("/api/users/search/posts/{idToken}/{email}")
    suspend fun getPostsOfUserSearch(@Path("idToken") idToken: String, @Path("email") email: String): Response<List<JSONPostModel>>

    @GET("/api/users/search/{searchQuery}")
    suspend fun searchForUsers(@Path("searchQuery") searchQuery : String): Response<List<JSONUserModel>>

    @POST("/api/users/{idToken}")
    suspend fun createUser(@Body requestBody: RequestBody, @Path("idToken") idToken: String): Response<ResponseBody>

    @POST("/api/users/posts/{idToken}")
    suspend fun createPost(@Body requestBody: RequestBody, @Path("idToken") idToken: String): Response<ResponseBody>

    @PUT("/api/users/{idToken}")
    suspend fun updateUser(@Body requestBody: RequestBody, @Path("idToken") idToken: String): Response<ResponseBody>

    @DELETE("/api/users/{idToken}")
    suspend fun deleteUser(@Path("idToken") idToken: String): Response<ResponseBody>

    @DELETE("/api/users/posts/{idToken}/{postId}")
    suspend fun deletePost(@Path("idToken") idToken: String, @Path("postId") postId: Long): Response<ResponseBody>

}