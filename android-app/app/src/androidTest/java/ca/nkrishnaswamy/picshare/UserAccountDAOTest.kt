package ca.nkrishnaswamy.picshare

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.db.roomDbs.CurrentLoggedInUserCache
import ca.nkrishnaswamy.picshare.data.models.relations.SignedInAccountWithUserPosts
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost
import com.google.common.truth.Truth.assertThat
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserAccountDAOTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: CurrentLoggedInUserCache
    private lateinit var dao: UserAccountDAO

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CurrentLoggedInUserCache::class.java).allowMainThreadQueries().build()
        dao = db.userAccountDAO()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertUserTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")
        dao.insertUser(newAccount)

        val loggedInUser = dao.retrieveCurrentLoggedInUser().getOrAwaitValue()

        assertThat(loggedInUser.username).isEqualTo(username)
        assertThat(loggedInUser.email).isEqualTo(email)
        assertThat(loggedInUser.name).isEqualTo(name)
    }

    @Test
    fun deleteUserTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")
        dao.insertUser(newAccount)
        dao.deleteAccount()

        val loggedInUser = dao.retrieveCurrentLoggedInUser().getOrAwaitValue()

        assertThat(loggedInUser).isNull()
    }

    @Test
    fun updateUserTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")

        dao.insertUser(newAccount)

        var loggedInAccount = dao.retrieveCurrentLoggedInUser().getOrAwaitValue()

        loggedInAccount.username = "stevieNEWUsername"

        dao.updateUser(loggedInAccount)

        loggedInAccount = dao.retrieveCurrentLoggedInUser().getOrAwaitValue()

        assertThat(loggedInAccount.username).isEqualTo("stevieNEWUsername")
    }

    @Test
    fun insertPostTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")

        dao.insertUser(newAccount)

        val caption = "this is a test caption!"
        val newPost = UserPost(0,caption, "", email)

        dao.insertPost(newPost)

        val listOfPosts = dao.getUserModelWithUserPostsNonLiveData()[0].userPosts

        assertThat(listOfPosts[0].caption).isEqualTo(caption)
    }

    @Test
    fun deletePostTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")

        dao.insertUser(newAccount)

        val newPost = UserPost(0,"this is a test caption!", "", email)

        dao.insertPost(newPost)

        dao.deletePost(newPost)

        val listOfPosts : List<SignedInAccountWithUserPosts> = dao.getUserModelWithUserPosts().getOrAwaitValue()

        assertThat(listOfPosts[0].userPosts).doesNotContain(newPost)
    }

    @Test
    fun deleteAllPostsTest() = runBlockingTest {
        val email = "stevenash0609@gmail.com"
        val username = "stevie0609"
        val name = "Steven"
        val newAccount = UserModel(0, email,username, name, "", "")

        dao.insertUser(newAccount)

        val newPost = UserPost(0,"this is a test caption!", "", email)
        val newPost2 = UserPost(0,"this is a test caption 2!", "", email)
        val newPost3 = UserPost(0,"this is a test caption 3!", "", email)

        dao.insertPost(newPost)
        dao.insertPost(newPost2)
        dao.insertPost(newPost3)

        dao.deleteAllPosts()

        val listOfPosts : List<SignedInAccountWithUserPosts> = dao.getUserModelWithUserPosts().getOrAwaitValue()

        assertThat(listOfPosts[0].userPosts).doesNotContain(newPost)
        assertThat(listOfPosts[0].userPosts).doesNotContain(newPost2)
        assertThat(listOfPosts[0].userPosts).doesNotContain(newPost3)
        assertThat(listOfPosts[0].userPosts).isEmpty()
    }

}