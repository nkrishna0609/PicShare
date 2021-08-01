package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import com.google.android.material.button.MaterialButton

class PickUsernameActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    lateinit var pickUsername: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_username)

        val user = intent.getParcelableExtra("userAccount") as? UserModel
        val password = intent.getStringExtra("password")

        pickUsername = findViewById(R.id.usernameET)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (!TextUtils.isEmpty(pickUsername.text)){
                val username = pickUsername.text.toString()
                val intent = Intent(this@PickUsernameActivity, WelcomeActivity::class.java)
                user?.username = username
                intent.putExtra("userAccount", user)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }
}