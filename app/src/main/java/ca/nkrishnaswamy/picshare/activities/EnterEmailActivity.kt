package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton

class EnterEmailActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val confirmationCodeIntent = Intent(this@EnterEmailActivity, EnterConfirmationCodeActivity::class.java)
            startActivity(confirmationCodeIntent)
        }
    }

    fun goToLoginPage(view: View){
        val loginIntent: Intent = Intent(this@EnterEmailActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}