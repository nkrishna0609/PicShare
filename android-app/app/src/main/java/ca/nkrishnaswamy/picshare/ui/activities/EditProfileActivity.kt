package ca.nkrishnaswamy.picshare.ui.activities

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var exitButton : ImageButton
    private lateinit var saveButton : ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel
    private lateinit var username : String
    private lateinit var fullName : String
    private lateinit var bio : String
    private lateinit var uriImg : Uri
    private lateinit var uriImgPathString : String
    private lateinit var nameET : TextInputEditText
    private lateinit var usernameET : TextInputEditText
    private lateinit var bioET : TextInputEditText
    private lateinit var user: UserModel
    private var changeCheck : Boolean = false

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val uriImg : Uri? = result.data?.data
        if (uriImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        profilePic.setImageDrawable(null)
        val takeFlags = result.data!!.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        CoroutineScope(Dispatchers.IO).launch {
            val resolver : ContentResolver = applicationContext.contentResolver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                resolver.takePersistableUriPermission(uriImg, takeFlags)
            }
        }
        user.setProfilePicPathFromUri(uriImg.toString())
        profilePic.setImageURI(Uri.parse(user.getProfilePicPathFromUri()))
        changeCheck = true
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val bitmapImg : Bitmap? = result.data?.getParcelableExtra("data")
        if (bitmapImg == null || result.resultCode != RESULT_OK) {
            return@registerForActivityResult
        }
        profilePic.setImageDrawable(null)
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
        profilePic.setImageURI(Uri.parse(user.getProfilePicPathFromUri()))
        changeCheck = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        exitButton = findViewById(R.id.exitButton)
        saveButton = findViewById(R.id.saveButton)
        profilePic = findViewById(R.id.profileImage)
        nameET = findViewById(R.id.nameET)
        usernameET = findViewById(R.id.usernameET)
        bioET = findViewById(R.id.bioET)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null){
                user = t
                username = t.getUsername()
                usernameET.setText(username)
                fullName = t.getName()
                nameET.setText(fullName)
                bio = t.getBio()
                bioET.setText(bio)
                uriImgPathString = t.getProfilePicPathFromUri()
                uriImg = Uri.parse(uriImgPathString)
                profilePic.setImageURI(uriImg)
            }
        })

        saveButton.setOnClickListener {
            val tilName : TextInputLayout = findViewById(R.id.tilName) //til = text input layout
            val tilUsername : TextInputLayout = findViewById(R.id.tilUsername)
            val usernameInET : String = usernameET.text.toString()
            val nameInET : String = nameET.text.toString()
            val bioInET : String = bioET.text.toString()

            tilName.helperText = ""
            tilUsername.helperText = ""

            if ((TextUtils.isEmpty(nameET.text)) || (TextUtils.isEmpty(usernameET.text))) {
                if ((TextUtils.isEmpty(nameET.text)) && (TextUtils.isEmpty(usernameET.text))) {
                    tilName.helperText = "Name is Empty"
                    tilUsername.helperText = "Username is Empty"
                } else if (TextUtils.isEmpty(nameET.text)) {
                    tilName.helperText = "Name is Empty"
                }
                else {
                    tilUsername.helperText = "Username is Empty"
                }
            } else{
                if (usernameInET != username) {
                    //must update username field in db - later must check if username is not taken by another user once I set up the Node/Express server
                    user.setUsername(usernameInET)
                    changeCheck = true
                }
                if (nameInET != fullName) {
                    user.setName(nameInET)
                    changeCheck = true
                }
                if (bioInET != bio) {
                    user.setBio(bioInET)
                    changeCheck = true
                }
                if (changeCheck) {
                    val context : Context = this
                    CoroutineScope(Dispatchers.IO).launch{
                        val currentSignedInFireBaseUser = authViewModel.getCurrentSignedInFirebaseUser()
                        val idToken = currentSignedInFireBaseUser?.let { user: FirebaseUser ->
                            authViewModel.getUserIdToken(user)
                        }
                        if (idToken != null) {
                            val check = signedInUserViewModel.updateUser(context, user, idToken)
                            if (check) {
                                withContext(Dispatchers.Main){
                                    val intentExit = Intent(this@EditProfileActivity, MainActivity::class.java)
                                    startActivity(intentExit)
                                    finish()
                                }
                            }
                        }
                    }
                }
            }

        }

        exitButton.setOnClickListener {
            val intentExit = Intent(this@EditProfileActivity, MainActivity::class.java)
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