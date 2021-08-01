package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PickUsernameActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    private lateinit var pickUsername: EditText
    private lateinit var signedInUserVM: SignedInUserViewModel
    private lateinit var errorMessageTV : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_username)

        signedInUserVM = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        val user = intent.getParcelableExtra("userAccount") as? UserModel
        val password = intent.getStringExtra("password")

        pickUsername = findViewById(R.id.usernameET)

        errorMessageTV = findViewById(R.id.errorMessage)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (!TextUtils.isEmpty(pickUsername.text)){
                val username = pickUsername.text.toString()
                CoroutineScope(Dispatchers.IO).launch{
                    when (signedInUserVM.checkIfUsernameIsAvailable(username)) {
                        0 -> {
                            withContext(Dispatchers.Main){
                                errorMessageTV.text = ""
                                val intent = Intent(this@PickUsernameActivity, WelcomeActivity::class.java)
                                user?.username = username
                                intent.putExtra("userAccount", user)
                                intent.putExtra("password", password)
                                startActivity(intent)
                            }
                        }
                        1 -> {
                            withContext(Dispatchers.Main){
                                errorMessageTV.text = "Username Not Available"
                            }
                        }
                        else -> {
                            withContext(Dispatchers.Main){
                                errorMessageTV.text = "Error. Try Again"
                            }
                        }
                    }
                }
            }
        }
    }
}