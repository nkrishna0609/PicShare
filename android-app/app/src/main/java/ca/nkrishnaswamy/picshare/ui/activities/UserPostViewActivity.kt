package ca.nkrishnaswamy.picshare.ui.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters.UserPostsAdapter
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserPostViewActivity : AppCompatActivity() {
    private lateinit var profilePic : CircleImageView
    private lateinit var usernameTV : TextView
    private lateinit var postPic : ImageView
    private lateinit var backButton : ImageButton
    private lateinit var optionsButton : ImageButton
    private lateinit var captionTV : TextView
    private lateinit var searchButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var post : UserPost
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var username : String
    private lateinit var profilePicStringUriPath : String
    private lateinit var postPicUriStringPath : String
    private lateinit var caption : String
    private lateinit var verticalMenuDialog : Dialog
    private lateinit var deletePostButton : AppCompatButton
    private lateinit var adapter : RecyclerView.Adapter<UserPostsAdapter.UserPostsViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_post)

        adapter = UserPostsAdapter(this)

        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        postPic = findViewById(R.id.postImage)
        backButton = findViewById(R.id.backButton)
        optionsButton = findViewById(R.id.menuButton)
        captionTV = findViewById(R.id.caption)
        searchButton = findViewById(R.id.searchPageButton)
        homeButton = findViewById(R.id.homeButton)

        verticalMenuDialog = BottomSheetDialog(this)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        post = intent.getParcelableExtra("post")!!
        caption = post.getCaption()
        postPicUriStringPath = post.getUriImgPathString()

        captionTV.text = caption
        postPic.setImageURI(Uri.parse(postPicUriStringPath))

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, {
            username = it.getUsername()
            usernameTV.text = it.getUsername()
            profilePicStringUriPath = it.getProfilePicPathFromUri()
            profilePic.setImageURI(Uri.parse(profilePicStringUriPath))
        })

        optionsButton.setOnClickListener {
            val verticalMenu = layoutInflater.inflate(R.layout.layout_modal_bottom_sheet_post, null)
            verticalMenuDialog.setContentView(verticalMenu)
            verticalMenuDialog.show()

            deletePostButton = verticalMenuDialog.findViewById(R.id.deletePostButton)

            deletePostButton.setOnClickListener {
                showDialogDeletePost()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this@UserPostViewActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        searchButton.setOnClickListener {
            val searchPageIntent = Intent(this@UserPostViewActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
            finish()
        }

        homeButton.setOnClickListener {
            val myAccountPageIntent: Intent = Intent(this@UserPostViewActivity, MainActivity::class.java)
            startActivity(myAccountPageIntent)
            finish()
        }

    }

    @Override
    override fun onDestroy() {
        super.onDestroy();
        verticalMenuDialog.dismiss();
    }

    private fun showDialogDeletePost() {
        CoroutineScope(Dispatchers.IO).launch{
            signedInUserViewModel.deletePost(post)
            adapter.notifyDataSetChanged()
            //val imgCache = File(profilePicStringUriPath)
            //if (imgCache.exists()) {
                //if (!imgCache.delete()) {
                    //imgCache.canonicalFile.delete()
                    //if (imgCache.exists()) {
                        //applicationContext.deleteFile(imgCache.name)
                    //}
                //}
            //}
        }
        val intent = Intent(this@UserPostViewActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}