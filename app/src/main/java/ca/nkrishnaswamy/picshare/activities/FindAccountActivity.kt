package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton

class FindAccountActivity : AppCompatActivity() {
    lateinit var nextButton: MaterialButton
    lateinit var editTextEnterEmail: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_account)

        editTextEnterEmail = findViewById(R.id.editTextEnterEmail)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (!TextUtils.isEmpty(editTextEnterEmail.text)){
                val email:String = editTextEnterEmail.text.toString()
                val intent = Intent(this@FindAccountActivity, ResetPasswordActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }
    }
}