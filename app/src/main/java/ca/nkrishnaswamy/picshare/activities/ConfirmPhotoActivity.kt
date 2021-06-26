package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class ConfirmPhotoActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_confirmation)

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val confirmaPhotoIntent = Intent(this@ConfirmPhotoActivity, MainActivity::class.java)
            startActivity(confirmaPhotoIntent)
        }
    }
}