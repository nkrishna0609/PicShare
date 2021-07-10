package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class FindAccountActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    lateinit var editTextEnterEmail: EditText
    lateinit var authViewModel: AuthViewModel
    lateinit var errorMessageTV : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_account)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        editTextEnterEmail = findViewById(R.id.editTextEnterEmail)

        errorMessageTV = findViewById(R.id.errorMessage)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val email:String = editTextEnterEmail.text.toString()
            if (TextUtils.isEmpty(editTextEnterEmail.text)){
                errorMessageTV.text = "Email Field is Empty"
            }
            else if (!checkFormatOfEmail(email)){
                errorMessageTV.text = "Email Format is Invalid"
            }
            else{
                CoroutineScope(Dispatchers.IO).launch{
                    val emailExistCheck: Boolean = checkIfEmailExistsAlready(email)
                    withContext(Dispatchers.Main){
                        if (!emailExistCheck){ //if email address is associated to an existing account
                            withContext((Dispatchers.IO)){
                                authViewModel.sendPasswordResetEmail(email)
                            }
                            val intent = Intent(this@FindAccountActivity, ResetPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        }
                        else{
                            errorMessageTV.text = "This Email does not Belong to an Existing Account"
                        }
                    }
                }
            }
        }
    }

    private fun checkFormatOfEmail(email: String): Boolean{
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return authViewModel.checkIfEmailExistsAlready(email)
    }
}