package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost
import ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters.SearchedAccountPostsAdapter

import de.hdodenhof.circleimageview.CircleImageView

class ProfileSearchResultActivity : AppCompatActivity() {
    private lateinit var user : UserModel
    private lateinit var postList : ArrayList<UserPost>
    private lateinit var searchPageButton: ImageButton
    private lateinit var myAccountPageButton: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var usernameTV : TextView
    private lateinit var fullNameTV : TextView
    private lateinit var bioTV : TextView
    private lateinit var postCountTV : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : SearchedAccountPostsAdapter
    private lateinit var postTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_search_result)

        recyclerView = findViewById(R.id.recyclerView)

        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        fullNameTV = findViewById(R.id.name)
        bioTV = findViewById(R.id.bio)
        postCountTV = findViewById(R.id.postNum)
        searchPageButton = findViewById(R.id.searchPageButton)
        myAccountPageButton = findViewById(R.id.myAccountPageButton)
        postTV = findViewById(R.id.postTV)

        user = intent.getParcelableExtra("searchedAccount")!!
        postList = intent.getParcelableArrayListExtra<UserPost>("postList") as ArrayList<UserPost>

        adapter = SearchedAccountPostsAdapter(this, user)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter.setPostList(postList)

        usernameTV.text = user.username
        fullNameTV.text = user.name
        bioTV.text = user.bio
        profilePic.setImageURI(Uri.parse(user.profilePicPathFromUri))
        postCountTV.text = postList.size.toString()
        if (postList.size == 1) {
            postTV.text = "Post"
        } else {
            postTV.text = "Posts"
        }

        myAccountPageButton.setOnClickListener {
            val myAccountPageIntent = Intent(this@ProfileSearchResultActivity, MainActivity::class.java)
            startActivity(myAccountPageIntent)
        }

        searchPageButton.setOnClickListener {
            val intent = Intent(this@ProfileSearchResultActivity, SearchAccountsActivity::class.java)
            startActivity(intent)
        }
    }
}