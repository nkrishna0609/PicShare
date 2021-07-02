package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import ca.nkrishnaswamy.picshare.models.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    private var user: UserModel? = null
    var password: String? = ""
    private var bitmapImg: Bitmap? = null
    private lateinit var searchPageButton: ImageButton
    private lateinit var usernameTV: TextView
    private lateinit var nameTV: TextView
    private lateinit var profilePic: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = intent.getParcelableExtra("userAccount") as? UserModel
        password = intent.getStringExtra("password")
        profilePic = findViewById(R.id.profileImage)
        bitmapImg = user?.getProfilePic()

        if (bitmapImg == null) {
            profilePic.setImageResource(R.drawable.profile_placeholder_pic)
        }
        else{
            profilePic.setImageBitmap(bitmapImg)
        }

        usernameTV = findViewById(R.id.username)
        usernameTV.text = user?.getUsername()
        nameTV = findViewById(R.id.name)
        nameTV.text = user?.getName()

        searchPageButton=findViewById(R.id.searchPageButton)
        searchPageButton.setOnClickListener {
            val searchPageIntent: Intent = Intent(this@MainActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
        }
    }
}