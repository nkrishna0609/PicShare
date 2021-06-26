package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun goToSignUpPage(view: View){
        val signUpIntent: Intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(signUpIntent)
    }

    fun forgotLogin(view: View){
        val forgotLoginIntent: Intent = Intent(this@LoginActivity, EnterConfirmationCodeActivity::class.java)
        startActivity(forgotLoginIntent)
    }
}