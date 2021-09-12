package ca.nkrishnaswamy.picshare.ui.activities

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class ConfirmPhotoActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    private lateinit var img: CircleImageView
    private lateinit var user: UserModel
    private lateinit var password: String
    private lateinit var signedInUserVM : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel
    private lateinit var selectedProfilePicPath: String
    private var lastPhotoTakenType = NO_PROFILE_PIC
    private lateinit var requestOptions : RequestOptions

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uriImg : Uri? = result.data?.data
        if (uriImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        img.setImageDrawable(null)
        val takeFlags = result.data!!.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        CoroutineScope(Dispatchers.IO).launch {
            val resolver : ContentResolver = applicationContext.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                resolver.takePersistableUriPermission(uriImg, takeFlags)
            }
        }
        user.profilePicPathFromUri = uriImg.toString()
        lastPhotoTakenType = LAST_PROFILE_PIC_FROM_GALLERY
        val intent = Intent(this@ConfirmPhotoActivity, ConfirmPhotoActivity::class.java)
        intent.putExtra("userAccount", user)
        intent.putExtra("password", password)
        intent.putExtra("lastPhotoTakenType", lastPhotoTakenType)
        startActivity(intent)
        finish()
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val bitmapImg : Bitmap? = result.data?.getParcelableExtra("data")
        if (bitmapImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        img.setImageDrawable(null)
        val file = File(applicationContext.cacheDir, "tempCacheProfilePic")
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

        user.profilePicPathFromUri = uriImg.toString()
        if (lastPhotoTakenType == LAST_PROFILE_PIC_FROM_GALLERY) {
            lastPhotoTakenType = LAST_PROFILE_PIC_FROM_CAMERA
            val intent = Intent(this@ConfirmPhotoActivity, ConfirmPhotoActivity::class.java)
            intent.putExtra("userAccount", user)
            intent.putExtra("password", password)
            intent.putExtra("lastPhotoTakenType", lastPhotoTakenType)
            startActivity(intent)
            finish()
        }
        else{
            lastPhotoTakenType = LAST_PROFILE_PIC_FROM_CAMERA
            Glide.with(this).load(Uri.parse(user.profilePicPathFromUri)).apply(requestOptions).into(img)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_confirmation)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        signedInUserVM = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        intent.getParcelableExtra<UserModel>("userAccount").also {
            if (it != null) {
                user = it
            }
        }
        password = intent.getStringExtra("password") as String
        lastPhotoTakenType = intent.getIntExtra("lastPhotoTakenType", NO_PROFILE_PIC)

        selectedProfilePicPath = user.profilePicPathFromUri

        img = findViewById(R.id.profilePic)
        requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
        Glide.with(this).load(Uri.parse(selectedProfilePicPath)).apply(requestOptions).into(img)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val email : String = user.email
            val context : Context = this
            CoroutineScope(Dispatchers.IO).launch{
                val idToken : String? = authViewModel.registerUserByEmailAndPassword(email, password)
                if (idToken != null) {
                    val check = signedInUserVM.registerUser(context, user, idToken)
                    if (check) {
                        withContext(Dispatchers.Main){
                            val intent = Intent(this@ConfirmPhotoActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
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