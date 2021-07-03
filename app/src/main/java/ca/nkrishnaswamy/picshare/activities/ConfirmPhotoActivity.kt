package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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

        user.setProfilePicPathFromUri(uriImg.toString())
        img.setImageDrawable(null)
        img.setImageURI(Uri.parse(selectedProfilePicPath))
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

        selectedProfilePicPath = user.getProfilePicPathFromUri()

        img = findViewById(R.id.profilePic)
        img.setImageURI(Uri.parse(selectedProfilePicPath))

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val email : String = user.getEmail()
            val confirmPhotoIntent = Intent(this@ConfirmPhotoActivity, MainActivity::class.java)
            CoroutineScope(Dispatchers.IO).launch{
                authViewModel.registerUserByEmailAndPassword(email, password)
                signedInUserVM.logInUser(user)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(confirmPhotoIntent)
        }
    }

    fun changePhoto(view: View){
        val buttonList  = arrayOf("Take Photo", "Choose From Library")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
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
                        val intentPickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickPhotoLauncher.launch(intentPickPhoto)
                    }
                }
            }
        }
        builder.show()
    }
}