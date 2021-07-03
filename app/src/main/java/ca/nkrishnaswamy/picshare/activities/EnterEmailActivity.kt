package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class EnterEmailActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    lateinit var emailEditText: EditText
    lateinit var newAccount: UserModel
    lateinit var authViewModel: AuthViewModel
    lateinit var errorMessageTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        emailEditText = findViewById(R.id.emailEditText)

        errorMessageTV = findViewById(R.id.errorMessage)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            if (TextUtils.isEmpty(emailEditText.text)){
                errorMessageTV.text = "Email Field is Empty"
            }
            else if (!checkFormatOfEmail(email)){
                errorMessageTV.text = "Email Format is Invalid"
            }
            else{
                CoroutineScope(IO).launch{
                    val emailExistCheck: Boolean = checkIfEmailExistsAlready(email)
                    withContext(Dispatchers.Main){
                        if (emailExistCheck){ //if email address is NOT associated to an existing account
                            errorMessageTV.text=""
                            newAccount = UserModel(email,"", "", "")
                            val intent = Intent(this@EnterEmailActivity, EnterNamePasswordActivity::class.java)
                            intent.putExtra("userAccount", newAccount)
                            startActivity(intent)

                        }
                        else{
                            errorMessageTV.text = "This Email is on Another Account"
                        }
                    }
                }
            }
        }
    }

    fun goToLoginPage(view: View){
        val loginIntent: Intent = Intent(this@EnterEmailActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun checkFormatOfEmail(email: String): Boolean{
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return authViewModel.checkIfEmailExistsAlready(email)
    }
}