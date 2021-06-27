package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.models.UserModel
import ca.nkrishnaswamy.picshare.viewmodels.AuthViewModel
import com.google.android.material.button.MaterialButton

class EnterEmailActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    lateinit var emailEditText: EditText
    lateinit var newAccount: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        emailEditText = findViewById(R.id.emailEditText)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (!TextUtils.isEmpty(emailEditText.text)){
                val email: String = emailEditText.text.toString()
                newAccount = UserModel(email,"", "")
                val intent = Intent(this@EnterEmailActivity, EnterNamePasswordActivity::class.java)
                intent.putExtra("userAccount", newAccount)
                startActivity(intent)
            }
        }
    }

    fun goToLoginPage(view: View){
        val loginIntent: Intent = Intent(this@EnterEmailActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}