package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEnterText: EditText
    private lateinit var passwordEnterText: EditText
    private lateinit var errorMessageTV : TextView
    private lateinit var loginButton : MaterialButton
    private lateinit var authViewModel: AuthViewModel
    private lateinit var signedInUserVM : SignedInUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        signedInUserVM = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        emailEnterText = findViewById(R.id.emailEnter)
        passwordEnterText = findViewById(R.id.passwordEnter)

        errorMessageTV = findViewById(R.id.errorMessage)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val email: String = emailEnterText.text.toString()
            val password: String = passwordEnterText.text.toString()
            if ((TextUtils.isEmpty(emailEnterText.text)) || (TextUtils.isEmpty(passwordEnterText.text))){
                errorMessageTV.text = "Email and/or Password are Empty"
            }
            else if (!checkFormatOfEmail(email)){
                errorMessageTV.text = "Email Format is Invalid"
            }
            else{
                CoroutineScope(Dispatchers.IO).launch{
                    val check: Boolean = authViewModel.loginWithEmailAndPassword(email, password)
                    if (!check){
                        withContext(Dispatchers.Main){
                            errorMessageTV.text = "Email and/or Password are Incorrect"
                        }
                    }
                    else{
                        errorMessageTV.text=""
                        CoroutineScope(Dispatchers.IO).launch{
                            val currentSignedInUser = authViewModel.getCurrentSignedInFirebaseUser()
                            val idToken = currentSignedInUser?.let { user: FirebaseUser ->
                                authViewModel.getUserIdToken(user)
                            }
                            //println("The User Id Token is: " + idToken);
                            //TODO: Retrieve account user from Node.js server with idToken
                            //we send the idToken to server and server will validate using Firebase and get a uid from it
                            //if uid is valid on server, it will send back user account to this app
                            //signedInUserVM.logInUser(account from Node.js server here)    //to store current user info into cache (local db)
                            if (idToken != null) {
                                signedInUserVM.logInUser(idToken)
                            }
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
        }

    }

    private fun checkFormatOfEmail(email: String): Boolean{
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun goToSignUpPage(view: View){
        val signUpIntent: Intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(signUpIntent)
    }

    fun forgotLogin(view: View){
        val forgotLoginIntent: Intent = Intent(this@LoginActivity, FindAccountActivity::class.java)
        startActivity(forgotLoginIntent)
    }
}