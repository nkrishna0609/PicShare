package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.nkrishnaswamy.picshare.data.models.UserModel
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class ConfirmPhotoActivity : AppCompatActivity() {
    private lateinit var nextButton: MaterialButton
    private lateinit var img: CircleImageView
    private var user: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic_confirmation)

        user = intent.getParcelableExtra("userAccount") as? UserModel

        val selectedProfilePicPath: String? = user?.getProfilePicPath()

        img = findViewById(R.id.profilePic)
        img.setImageURI(Uri.parse(selectedProfilePicPath))

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            val confirmPhotoIntent = Intent(this@ConfirmPhotoActivity, MainActivity::class.java)
            intent.putExtra("userAccount", user)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(confirmPhotoIntent)
        }
    }
}