package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import ca.nkrishnaswamy.picshare.models.UserModel
import com.google.android.material.button.MaterialButton

class EnterNamePasswordActivity : AppCompatActivity() {
    private lateinit var continueButton: MaterialButton
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name_password)

        val user = intent.getParcelableExtra("userAccount") as? UserModel

        nameEditText=findViewById(R.id.nameEditText)
        passwordEditText=findViewById(R.id.passwordEditText)

        continueButton = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            if ((!TextUtils.isEmpty(nameEditText.text)) && (!TextUtils.isEmpty(passwordEditText.text))){
                val name: String = nameEditText.text.toString()
                val password: String = passwordEditText.text.toString()
                user?.setName(name)
                val intent = Intent(this@EnterNamePasswordActivity, PickUsernameActivity::class.java)
                intent.putExtra("userAccount", user)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }
}