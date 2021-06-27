package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.models.UserModel
import ca.nkrishnaswamy.picshare.viewmodels.AuthViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddProfilePhotoActivity : AppCompatActivity() {
    lateinit var addPhotoButton: MaterialButton
    var user: UserModel? = null
    var password: String = ""
    lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_add)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        user = intent.getParcelableExtra("userAccount") as? UserModel

        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPhotoButton.setOnClickListener {
            val addPhoto: Intent = Intent(this@AddProfilePhotoActivity, ConfirmPhotoActivity::class.java)
            startActivity(addPhoto)
        }
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