package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var backButton: ImageButton
    lateinit var resetPasswordTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val email = intent.getStringExtra("email")

        resetPasswordTV = findViewById(R.id.resetPswdEmailTextView)
        val textMsg: String = resetPasswordTV.text.toString()+email+"."
        resetPasswordTV.text = textMsg

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}