package ca.nkrishnaswamy.picshare.ui.activities

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class NewPostActivity : AppCompatActivity() {
    private lateinit var cancelButton : ImageButton
    private lateinit var completePostButton : ImageButton
    private lateinit var captionET : EditText
    private lateinit var postPhoto : ImageView
    private lateinit var uriImg : Uri
    private lateinit var uriImgPathString : String
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private var postNum : Int = 0
    private lateinit var user: UserModel
    private lateinit var post: UserPost
    private lateinit var fileChildName : String

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uriImg : Uri? = result.data?.data
        if (uriImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        postPhoto.setImageDrawable(null)
        val takeFlags = result.data!!.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        CoroutineScope(Dispatchers.IO).launch {
            val resolver : ContentResolver = applicationContext.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                resolver.takePersistableUriPermission(uriImg, takeFlags)
            }
        }
        uriImgPathString = uriImg.toString()
        postPhoto.setImageURI(Uri.parse(uriImgPathString))
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val bitmapImg : Bitmap? = result.data?.getParcelableExtra("data")
        if (bitmapImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        postPhoto.setImageDrawable(null)
        val file = File(applicationContext.cacheDir, fileChildName)
        file.delete()
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

        uriImgPathString = uriImg.toString()
        postPhoto.setImageURI(Uri.parse(uriImgPathString))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        cancelButton = findViewById(R.id.cancel_button)
        completePostButton = findViewById(R.id.confirm_button)
        captionET = findViewById(R.id.postCaption)
        postPhoto = findViewById(R.id.picture)

        post = intent.getParcelableExtra("post")!!
        fileChildName = intent.getStringExtra("fileChildName").toString()
        uriImgPathString = post.getUriImgPathString()
        uriImg = Uri.parse(uriImgPathString)
        postPhoto.setImageURI(uriImg)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, {
            if (it != null) {
                user = it
                postNum = user.getPostsNum()
            }
        })

        completePostButton.setOnClickListener {
            val caption: String = captionET.text.toString()
            if (TextUtils.isEmpty(captionET.text)){
                Toast.makeText(baseContext, "Write a Caption", Toast.LENGTH_LONG).show()
            }
            else {
                post.setCaption(caption)
                CoroutineScope(Dispatchers.IO).launch {
                    signedInUserViewModel.addPost(post)
                    postNum++
                    user.setPostsNum(postNum)
                    signedInUserViewModel.updateUser(user)
                }
                val intentExit = Intent(this@NewPostActivity, MainActivity::class.java)
                startActivity(intentExit)
                finish()
            }
        }

        cancelButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val picCache = File(uriImgPathString)
                if (picCache.exists()) {
                    if (!picCache.delete()) {
                        picCache.canonicalFile.delete()
                        if (picCache.exists()) {
                            applicationContext.deleteFile(picCache.name)
                        }
                    }
                }
            }
            val intentExit = Intent(this@NewPostActivity, MainActivity::class.java)
            startActivity(intentExit)
            finish()
        }
    }

    fun changePhoto(view: View){
        val buttonList  = arrayOf("Take Photo", "Choose From Library")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppCompat_Dialog_Alert
        )
        builder.setTitle("Change Profile Photo")

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
}