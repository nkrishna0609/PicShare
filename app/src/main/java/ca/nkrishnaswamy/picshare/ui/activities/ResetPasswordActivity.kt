package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var backButton: ImageButton
    lateinit var resetPasswordTV: TextView
    lateinit var sendResetPswdEmailTV : TextView
    lateinit var authViewModel: AuthViewModel
    lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        email = intent.getStringExtra("email") as String

        sendResetPswdEmailTV = findViewById(R.id.resetEmailClickTV)

        resetPasswordTV = findViewById(R.id.resetPswdEmailTextView)
        val textMsg: String = resetPasswordTV.text.toString()+email+"."
        resetPasswordTV.text = textMsg

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun resendEmail(view: View){
        CoroutineScope(Dispatchers.IO).launch{
            authViewModel.sendPasswordResetEmail(email)
        }
    }
}