package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import com.google.android.material.button.MaterialButton

class EnterNamePasswordActivity : AppCompatActivity() {
    private lateinit var continueButton: MaterialButton
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorMessageTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name_password)

        val user = intent.getParcelableExtra("userAccount") as? UserModel

        nameEditText=findViewById(R.id.nameEditText)
        passwordEditText=findViewById(R.id.passwordEditText)

        errorMessageTV=findViewById(R.id.errorMessage)

        continueButton = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val password: String = passwordEditText.text.toString()
            if ((TextUtils.isEmpty(nameEditText.text)) || (TextUtils.isEmpty(passwordEditText.text))){
                errorMessageTV.text = "Name and/or Password are Empty"
            }
            else if (!checkPasswordCharLength(password)){
                errorMessageTV.text = "Password must be at least 6 characters long"
            }
            else{
                errorMessageTV.text=""
                val name: String = nameEditText.text.toString()
                user?.name = name
                val intent = Intent(this@EnterNamePasswordActivity, PickUsernameActivity::class.java)
                intent.putExtra("userAccount", user)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }

    private fun checkPasswordCharLength(password: String):Boolean {
        return (password.length>=6)
    }

}