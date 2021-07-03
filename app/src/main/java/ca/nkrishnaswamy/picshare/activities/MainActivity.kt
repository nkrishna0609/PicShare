package ca.nkrishnaswamy.picshare

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var searchPageButton: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel
    private lateinit var email : String
    private lateinit var username : String
    private lateinit var fullName : String
    private lateinit var uriImg : Uri
    private lateinit var usernameTV : TextView
    private lateinit var fullNameTV : TextView
    private lateinit var editProfileButton : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        fullNameTV = findViewById(R.id.name)
        searchPageButton=findViewById(R.id.searchPageButton)
        editProfileButton=findViewById(R.id.editProfileButton)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null){
                email = t.getEmail()
                username = t.getUsername()
                usernameTV.text = username
                fullName = t.getName()
                fullNameTV.text = fullName
                uriImg = Uri.parse(t.getProfilePicPathFromUri())
                profilePic.setImageURI(uriImg)
            }
        })

        searchPageButton.setOnClickListener {
            val searchPageIntent: Intent = Intent(this@MainActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
        }
    }
}