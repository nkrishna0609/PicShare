package ca.nkrishnaswamy.picshare.ui.activities

import android.app.Dialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost
import ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters.UserPostsAdapter
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var user : UserModel
    private lateinit var searchPageButton: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel
    private lateinit var email : String
    private lateinit var username : String
    private lateinit var fullName : String
    private lateinit var bio : String
    private lateinit var uriImg : Uri
    private lateinit var uriImgPathString : String
    private lateinit var usernameTV : TextView
    private lateinit var fullNameTV : TextView
    private lateinit var bioTV : TextView
    private lateinit var editProfileButton : MaterialButton
    private lateinit var verticalPopUpMenuButton : ImageButton
    private lateinit var newPostButton : ImageButton
    private lateinit var logOutButton : AppCompatButton
    private lateinit var deleteAccountButton : AppCompatButton
    private lateinit var verticalMenuDialog : Dialog
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : UserPostsAdapter
    private lateinit var postCountTV : TextView
    private lateinit var postTV : TextView

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uriImg : Uri? = result.data?.data
        if (uriImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        val newPost = UserPost(0,"", "", email)
        val takeFlags = result.data!!.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        CoroutineScope(Dispatchers.IO).launch {
            val resolver : ContentResolver = applicationContext.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                resolver.takePersistableUriPermission(uriImg, takeFlags)
            }
        }
        newPost.uriImgPathString = uriImg.toString()
        val intent = Intent(this@MainActivity, NewPostActivity::class.java)
        intent.putExtra("post", newPost)
        startActivity(intent)
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val bitmapImg : Bitmap? = result.data?.getParcelableExtra("data")
        if (bitmapImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        val newPost = UserPost(0,"", "", email)
        var fileChildName = "tempCachePostPic" + getRandomString(20)
        var file = File(applicationContext.cacheDir, fileChildName)
        while (file.exists()) {
            fileChildName = "tempCachePostPic" + getRandomString(20)
            file = File(applicationContext.cacheDir, "tempCachePostPic" + fileChildName)
        }
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()

        val uriImg = file.toURI()
        newPost.uriImgPathString = uriImg.toString()
        val intent = Intent(this@MainActivity, NewPostActivity::class.java)
        intent.putExtra("post", newPost)
        intent.putExtra("fileChildName", fileChildName)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = UserPostsAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        
        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        fullNameTV = findViewById(R.id.name)
        bioTV = findViewById(R.id.bio)
        postCountTV = findViewById(R.id.postCountTV)
        searchPageButton = findViewById(R.id.searchPageButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        verticalPopUpMenuButton = findViewById(R.id.verticalPopUpMenu)
        newPostButton = findViewById(R.id.newPostButton)
        postTV = findViewById(R.id.postTV)

        verticalMenuDialog = BottomSheetDialog(this)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null){
                user = t
                email = t.email
                username = t.username
                usernameTV.text = username
                fullName = t.name
                fullNameTV.text = fullName
                bio = t.bio
                bioTV.text = bio
                uriImgPathString = t.profilePicPathFromUri
                uriImg = Uri.parse(uriImgPathString)
                profilePic.setImageURI(uriImg)
            }
            else{
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

        signedInUserViewModel.getPosts().observe(this, {
            if (it != null && it.isNotEmpty()) {
                postCountTV.text = it[0].userPosts.size.toString()
                if (it[0].userPosts.size == 1) {
                    postTV.text = "Post"
                } else {
                    postTV.text = "Posts"
                }
                adapter.setPostList(it[0].userPosts)
            }
        })

        editProfileButton.setOnClickListener {
            val intentEditProfile = Intent(this@MainActivity, EditProfileActivity::class.java)
            startActivity(intentEditProfile)
        }

        verticalPopUpMenuButton.setOnClickListener {
            val verticalMenu = layoutInflater.inflate(R.layout.layout_modal_bottom_sheet_account, null)
            verticalMenuDialog.setContentView(verticalMenu)
            verticalMenuDialog.show()

            logOutButton = verticalMenuDialog.findViewById(R.id.logOutButton)
            deleteAccountButton = verticalMenuDialog.findViewById(R.id.deleteAccountButton)

            logOutButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch{
                    signedInUserViewModel.logOutUser(applicationContext)
                    authViewModel.signOutUserFromFirebase()
                }
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

            deleteAccountButton.setOnClickListener {
                showDialogDeleteAccount()
            }
        }

        newPostButton.setOnClickListener {
            showDialogPhotoOption()
        }

        searchPageButton.setOnClickListener {
            val searchPageIntent = Intent(this@MainActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
        }
    }

    @Override
    override fun onDestroy() {
        super.onDestroy();
        verticalMenuDialog.dismiss();
    }

    private fun showDialogPhotoOption() {
        val buttonList  = arrayOf("Take Photo", "Choose From Library")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppCompat_Dialog_Alert
        )
        builder.setTitle("Select Picture")

        builder.setItems(buttonList) { _, which ->
            when(which) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val intentTakePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePhotoLauncher.launch(intentTakePhoto)
                    }
                }
                1 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        lateinit var intentPickPhoto : Intent
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intentPickPhoto = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            intentPickPhoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        }
                        else{
                            intentPickPhoto = Intent(Intent.ACTION_GET_CONTENT)
                        }
                        intentPickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                        intentPickPhoto.type = "image/*"
                        intentPickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        pickPhotoLauncher.launch(intentPickPhoto)
                    }
                }
            }
        }
        builder.show()
    }

    private fun showDialogDeleteAccount() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppCompat_Dialog_Alert
        )
        builder.setTitle("Delete " + username +  "?")
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch{
                val fireBaseCurrentLoggedInUser = authViewModel.getCurrentSignedInFirebaseUser()
                if (fireBaseCurrentLoggedInUser != null) {
                    val idToken = authViewModel.getUserIdToken()
                    val check = authViewModel.deleteAccountFromFirebase(fireBaseCurrentLoggedInUser)
                    if (check && idToken!=null) {
                        val checkDelete = signedInUserViewModel.deleteAccount(applicationContext, idToken)
                        if (checkDelete){
                            withContext(Dispatchers.Main) {
                                Toast.makeText(baseContext, "Account Deleted", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        })
        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun getRandomString(length : Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { charset.random() }.joinToString("")
    }

}