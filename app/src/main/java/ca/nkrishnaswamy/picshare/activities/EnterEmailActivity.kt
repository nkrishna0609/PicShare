package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.models.UserModel
import ca.nkrishnaswamy.picshare.viewmodels.AuthViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        errorMessageTV.visibility = View.INVISIBLE

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (!TextUtils.isEmpty(emailEditText.text)){
                val email: String = emailEditText.text.toString()
                    CoroutineScope(IO).launch{
                    val emailExistCheck: Boolean = checkIfEmailExistsAlready(email)
                    withContext(Dispatchers.Main){
                        if (emailExistCheck){
                            newAccount = UserModel(email,"", "")
                            val intent = Intent(this@EnterEmailActivity, EnterNamePasswordActivity::class.java)
                            intent.putExtra("userAccount", newAccount)
                            startActivity(intent)

                        }
                        else{
                            errorMessageTV.visibility = View.VISIBLE
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

    private suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return authViewModel.checkIfEmailExistsAlready(email)
    }
}