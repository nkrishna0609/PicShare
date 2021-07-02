package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewmodels.AuthViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class AddProfilePhotoActivity : AppCompatActivity() {
    private lateinit var addPhotoButton: MaterialButton
    private var user: UserModel? = null
    var password: String = ""
    private lateinit var authViewModel: AuthViewModel

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

        user?.setProfilePic(uriImg.toString())
        val intent = Intent(this@AddProfilePhotoActivity, ConfirmPhotoActivity::class.java)
        intent.putExtra("userAccount", user)
        intent.putExtra("password", password)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_add)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        user = intent.getParcelableExtra("userAccount") as? UserModel

        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPhotoButton.setOnClickListener {
            showDialogPhotoOption()
        }
    }

    private fun showDialogPhotoOption() {
        val buttonList  = arrayOf("Take Photo", "Choose From Library")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
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
                        val intentPickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickPhotoLauncher.launch(intentPickPhoto)
                    }
                }
            }
        }
        builder.show()
    }

    fun skipAddingPhoto(view: View){
        password = intent.getStringExtra("password") as String
        val email :String = user?.getEmail() as String
        CoroutineScope(IO).launch{
            authViewModel.registerUserByEmailAndPassword(email, password)
        }
        val intent = Intent(this@AddProfilePhotoActivity, MainActivity::class.java)
        intent.putExtra("userAccount", user)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}