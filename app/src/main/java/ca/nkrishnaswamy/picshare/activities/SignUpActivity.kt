package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun goToLoginPage(view: View){
        val loginIntent: Intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    fun emailSignUp(view: View){
        val emailEnterIntent: Intent = Intent(this@SignUpActivity, EnterEmailActivity::class.java)
        startActivity(emailEnterIntent)
    }
}