package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class EnterConfirmationCodeActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_confirmation_code)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val confirmationCodeIntent = Intent(this@EnterConfirmationCodeActivity, EnterNamePasswordActivity::class.java)
            startActivity(confirmationCodeIntent)
        }
    }
}