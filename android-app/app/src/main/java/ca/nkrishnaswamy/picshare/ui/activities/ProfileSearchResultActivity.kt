package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel

import de.hdodenhof.circleimageview.CircleImageView

class ProfileSearchResultActivity : AppCompatActivity() {
    private lateinit var user : UserModel
    private lateinit var searchPageButton: ImageButton
    private lateinit var myAccountPageButton: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var usernameTV : TextView
    private lateinit var fullNameTV : TextView
    private lateinit var bioTV : TextView
    private lateinit var postCountTV : TextView
    private lateinit var followingCountTV : TextView
    private lateinit var followersCountTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_search_result)

        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        fullNameTV = findViewById(R.id.name)
        bioTV = findViewById(R.id.bio)
        postCountTV = findViewById(R.id.postNum)
        followersCountTV = findViewById(R.id.followersNum)
        followingCountTV = findViewById(R.id.followingNum)
        searchPageButton = findViewById(R.id.searchPageButton)
        myAccountPageButton = findViewById(R.id.myAccountPageButton)

        user = intent.getParcelableExtra("searchedAccount")!!

        usernameTV.text = user.username
        fullNameTV.text = user.name
        bioTV.text = user.bio
        followersCountTV.text = user.followerNum.toString()
        followingCountTV.text = user.followingNum.toString()
        profilePic.setImageURI(Uri.parse(user.profilePicPathFromUri))

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