package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ca.nkrishnaswamy.picshare.R
import com.google.android.material.button.MaterialButton

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpButton : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signUpButton = findViewById(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val emailEnterIntent: Intent = Intent(this@SignUpActivity, EnterEmailActivity::class.java)
            startActivity(emailEnterIntent)
        }
    }

    fun goToLoginPage(view: View){
        val loginIntent = Intent(this@SignUpActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}