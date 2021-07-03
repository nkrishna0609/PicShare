package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmPhotoActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    private lateinit var img: CircleImageView
    private lateinit var user: UserModel
    private lateinit var password: String
    private lateinit var signedInUserVM : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel

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

        val selectedProfilePicPath: String = user.getProfilePicPathFromUri()

        img = findViewById(R.id.profilePic)
        img.setImageURI(Uri.parse(selectedProfilePicPath))

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val email : String = user.getEmail()
            val confirmPhotoIntent = Intent(this@ConfirmPhotoActivity, MainActivity::class.java)
            CoroutineScope(Dispatchers.IO).launch{
                authViewModel.registerUserByEmailAndPassword(email, password)
                signedInUserVM.logInNewUser(user)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(confirmPhotoIntent)
        }
    }
}