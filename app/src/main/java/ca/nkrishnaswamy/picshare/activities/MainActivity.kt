package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import ca.nkrishnaswamy.picshare.models.UserModel

class MainActivity : AppCompatActivity() {
    var user: UserModel? = null
    var password: String? = ""
    lateinit var searchPageButton: ImageButton
    lateinit var usernameTV: TextView
    lateinit var nameTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = intent.getParcelableExtra("userAccount") as? UserModel
        password = intent.getStringExtra("password")

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