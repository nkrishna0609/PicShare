package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    private lateinit var usernameTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_page)

        val user = intent.getParcelableExtra("userAccount") as? UserModel
        val password = intent.getStringExtra("password")

        usernameTextView = findViewById(R.id.username)
        usernameTextView.text = user?.getUsername()

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, AddProfilePhotoActivity::class.java)
            intent.putExtra("userAccount", user)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }
}