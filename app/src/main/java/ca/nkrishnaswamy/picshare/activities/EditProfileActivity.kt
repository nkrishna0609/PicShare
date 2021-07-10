package ca.nkrishnaswamy.picshare.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    private lateinit var exitButton : ImageButton
    private lateinit var saveButton : ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var username : String
    private lateinit var fullName : String
    private lateinit var uriImg : Uri
    private lateinit var uriImgPathString : String
    private lateinit var nameET : TextInputEditText
    private lateinit var usernameET : TextInputEditText
    private lateinit var bioET : TextInputEditText
    private lateinit var user: UserModel

    private var typeOfProfilePic = NO_PROFILE_PIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null) {
                user = t
            }
        })

        exitButton = findViewById(R.id.exitButton)
        saveButton = findViewById(R.id.saveButton)
        profilePic = findViewById(R.id.profileImage)
        nameET = findViewById(R.id.nameET)
        usernameET = findViewById(R.id.usernameET)
        bioET = findViewById(R.id.bioET)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null){
                username = t.getUsername()
                usernameET.setText(username)
                fullName = t.getName()
                nameET.setText(fullName)
                typeOfProfilePic = t.getTypeOfProfilePic()
                uriImgPathString = t.getProfilePicPathFromUri()
                uriImg = Uri.parse(uriImgPathString)
                profilePic.setImageURI(uriImg)
            }
        })

        saveButton.setOnClickListener {
            val tilName : TextInputLayout = findViewById(R.id.tilName)
            val tilUsername : TextInputLayout = findViewById(R.id.tilUsername)
            val usernameInET : String = usernameET.text.toString()
            val nameInET : String = nameET.text.toString()

            tilName.helperText = ""
            tilUsername.helperText = ""

            if ((TextUtils.isEmpty(nameET.text)) || (TextUtils.isEmpty(usernameET.text))) {
                if ((TextUtils.isEmpty(nameET.text)) && (TextUtils.isEmpty(usernameET.text))) {
                    tilName.helperText = "Name is Empty"
                    tilUsername.helperText = "Username is Empty"
                } else if (TextUtils.isEmpty(nameET.text)) {
                    tilName.helperText = "Name is Empty"
                }
                else {
                    tilUsername.helperText = "Username is Empty"
                }
            } else{
                var check: Boolean = false
                if (usernameInET != username) {
                    //must update username field in db - later must check if username is not taken by another user once I set up the Node/Express server
                    user.setUsername(usernameInET)
                    check = true
                }
                if (nameInET != fullName) {
                    user.setName(nameInET)
                    check = true
                }
                if (check) {
                    CoroutineScope(Dispatchers.IO).launch{
                        signedInUserViewModel.updateUser(user)
                    }
                }
                val intentExit = Intent(this@EditProfileActivity, MainActivity::class.java)
                startActivity(intentExit)
                finish()
            }

        }

        exitButton.setOnClickListener {
            val intentExit = Intent(this@EditProfileActivity, MainActivity::class.java)
            startActivity(intentExit)
            finish()
        }
    }
}