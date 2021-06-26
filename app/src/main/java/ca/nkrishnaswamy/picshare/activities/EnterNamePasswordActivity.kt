package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class EnterNamePasswordActivity : AppCompatActivity() {
    private lateinit var continueButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name_password)

        continueButton = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val enterNamePasswordActivity = Intent(this@EnterNamePasswordActivity, WelcomeActivity::class.java)
            startActivity(enterNamePasswordActivity)
        }
    }
}