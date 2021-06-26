package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton

class AddProfilePhotoActivity : AppCompatActivity() {
    lateinit var addPhotoButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_add)

        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPhotoButton.setOnClickListener {
            val addPhoto: Intent = Intent(this@AddProfilePhotoActivity, ConfirmPhotoActivity::class.java)
            startActivity(addPhoto)
        }
    }

    fun skipAddingPhoto(view: View){
        val skipAddingPhoto: Intent = Intent(this@AddProfilePhotoActivity, MainActivity::class.java)
        startActivity(skipAddingPhoto)
    }
}