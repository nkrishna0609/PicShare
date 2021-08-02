package ca.nkrishnaswamy.picshare.ui.activities

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

const val LAST_PROFILE_PIC_FROM_CAMERA = 1
const val LAST_PROFILE_PIC_FROM_GALLERY = 2
const val NO_PROFILE_PIC = 0

class AddProfilePhotoActivity : AppCompatActivity() {
    private lateinit var addPhotoButton: MaterialButton
    private lateinit var user: UserModel
    private lateinit var password: String
    private lateinit var authViewModel: AuthViewModel
    private lateinit var signedInUserVM : SignedInUserViewModel
    private var lastPhotoTakenType = NO_PROFILE_PIC

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uriImg : Uri? = result.data?.data
        if (uriImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        val takeFlags = result.data!!.flags and FLAG_GRANT_READ_URI_PERMISSION
        CoroutineScope(IO).launch {
            val resolver : ContentResolver = applicationContext.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                resolver.takePersistableUriPermission(uriImg, takeFlags)
            }
        }
        user.profilePicPathFromUri = uriImg.toString()
        lastPhotoTakenType = LAST_PROFILE_PIC_FROM_GALLERY
        val intent = Intent(this@AddProfilePhotoActivity, ConfirmPhotoActivity::class.java)
        intent.putExtra("userAccount", user)
        intent.putExtra("password", password)
        intent.putExtra("lastPhotoTakenType", lastPhotoTakenType)
        startActivity(intent)
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val bitmapImg : Bitmap? = result.data?.getParcelableExtra("data")
        if (bitmapImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
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
        lastPhotoTakenType = LAST_PROFILE_PIC_FROM_CAMERA
        val intent = Intent(this@AddProfilePhotoActivity, ConfirmPhotoActivity::class.java)
        intent.putExtra("userAccount", user)
        intent.putExtra("password", password)
        intent.putExtra("lastPhotoTakenType", lastPhotoTakenType)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_add)

        password = intent.getStringExtra("password") as String

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        signedInUserVM = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        intent.getParcelableExtra<UserModel?>("userAccount").also {
            if (it != null) {
                user = it
            }
        }

        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPhotoButton.setOnClickListener {
            showDialogPhotoOption()
        }
    }

    private fun showDialogPhotoOption() {
        val buttonList  = arrayOf("Take Photo", "Choose From Library")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppCompat_Dialog_Alert
        )
        builder.setTitle("Change Profile Photo")

        builder.setItems(buttonList) { _, which ->
            when(which) {
                0 -> {
                    CoroutineScope(IO).launch {
                        val intentTakePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePhotoLauncher.launch(intentTakePhoto)
                    }
                }
                1 -> {
                    CoroutineScope(IO).launch {
                        lateinit var intentPickPhoto : Intent
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intentPickPhoto = Intent(ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            intentPickPhoto.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                        }
                        else{
                            intentPickPhoto = Intent(Intent.ACTION_GET_CONTENT)
                        }
                        intentPickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                        intentPickPhoto.type = "image/*"
                        intentPickPhoto.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                        pickPhotoLauncher.launch(intentPickPhoto)
                    }
                }
            }
        }
        builder.show()
    }

    fun skipAddingPhoto(view: View){
        val uriDefaultImgString = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(
            R.drawable.profile_placeholder_pic
        ) + '/' + resources.getResourceTypeName(R.drawable.profile_placeholder_pic) + '/' + resources.getResourceEntryName(
            R.drawable.profile_placeholder_pic
        )
        user.profilePicPathFromUri = uriDefaultImgString
        val email :String = user.email
        val context : Context = this
        CoroutineScope(IO).launch{
            val idToken : String? = authViewModel.registerUserByEmailAndPassword(email, password)
            if (idToken != null) {
                val check = signedInUserVM.registerUser(context, user, idToken)
                if (check) {
                    withContext(Main){
                        val intent = Intent(this@AddProfilePhotoActivity, MainActivity::class.java)
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}