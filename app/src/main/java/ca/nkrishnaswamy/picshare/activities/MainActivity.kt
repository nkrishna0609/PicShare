package ca.nkrishnaswamy.picshare.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var searchPageButton: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var signedInUserViewModel : SignedInUserViewModel
    private lateinit var authViewModel : AuthViewModel
    private lateinit var email : String
    private lateinit var username : String
    private lateinit var fullName : String
    private lateinit var uriImg : Uri
    private lateinit var uriImgPathString : String
    private var typeOfProfilePic = NO_PROFILE_PIC
    private lateinit var usernameTV : TextView
    private lateinit var fullNameTV : TextView
    private lateinit var editProfileButton : MaterialButton
    private lateinit var verticalPopUpMenuButton : ImageButton
    private lateinit var logOutButton : AppCompatButton
    private lateinit var deleteAccountButton : AppCompatButton
    private lateinit var verticalMenuDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signedInUserViewModel = ViewModelProvider(this).get(SignedInUserViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        
        profilePic = findViewById(R.id.profileImage)
        usernameTV = findViewById(R.id.username)
        fullNameTV = findViewById(R.id.name)
        searchPageButton = findViewById(R.id.searchPageButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        verticalPopUpMenuButton = findViewById(R.id.verticalPopUpMenu)

        verticalMenuDialog = BottomSheetDialog(this)

        signedInUserViewModel.getCurrentLoggedInUser().observe(this, { t ->
            if (t != null){
                email = t.getEmail()
                username = t.getUsername()
                usernameTV.text = username
                fullName = t.getName()
                fullNameTV.text = fullName
                typeOfProfilePic = t.getTypeOfProfilePic()
                uriImgPathString = t.getProfilePicPathFromUri()
                uriImg = Uri.parse(uriImgPathString)
                profilePic.setImageURI(uriImg)
            }
        })

        editProfileButton.setOnClickListener {
            val intentEditProfile = Intent(this@MainActivity, EditProfileActivity::class.java)
            startActivity(intentEditProfile)
        }

        verticalPopUpMenuButton.setOnClickListener {
            val verticalMenu = layoutInflater.inflate(R.layout.layout_modal_bottom_sheel, null)
            verticalMenuDialog.setContentView(verticalMenu)
            verticalMenuDialog.show()

            logOutButton = verticalMenuDialog.findViewById(R.id.logOutButton)
            deleteAccountButton = verticalMenuDialog.findViewById(R.id.deleteAccountButton)

            logOutButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch{
                    signedInUserViewModel.signOut()
                    authViewModel.signOutUserFromFirebase()
                }
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

            deleteAccountButton.setOnClickListener {
                showDialogDeleteAccount()
            }
        }

        searchPageButton.setOnClickListener {
            val searchPageIntent = Intent(this@MainActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
        }
    }

    @Override
    override fun onDestroy() {
        super.onDestroy();
        verticalMenuDialog.dismiss();
    }

    private fun showDialogDeleteAccount() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppCompat_Dialog_Alert
        )
        builder.setTitle("Delete " + username +  "?")
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch{
                val fireBaseCurrentLoggedInUser = authViewModel.getCurrentSignedInFirebaseUser()
                if (fireBaseCurrentLoggedInUser != null) {
                    val check = authViewModel.deleteAccountFromFirebase(fireBaseCurrentLoggedInUser)
                    withContext(Dispatchers.Main) {
                        if (check) {
                            Toast.makeText(baseContext, "Account Deleted.", Toast.LENGTH_LONG).show()
                            CoroutineScope(Dispatchers.IO).launch{
                                val profilePicCache = File(uriImgPathString)
                                if (typeOfProfilePic == LAST_PROFILE_PIC_FROM_CAMERA &&  profilePicCache.exists()) {
                                    if (!profilePicCache.delete()) {
                                        profilePicCache.canonicalFile.delete()
                                        if (profilePicCache.exists()) {
                                            applicationContext.deleteFile(profilePicCache.name)
                                        }
                                    }
                                }
                                signedInUserViewModel.signOut()
                                authViewModel.signOutUserFromFirebase()
                            }
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

}