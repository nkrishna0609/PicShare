package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import ca.nkrishnaswamy.picshare.R

class SearchAccountsActivity : AppCompatActivity() {
    lateinit var myAccountPageButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profiles)

        myAccountPageButton=findViewById(R.id.myAccountPageButton)
        myAccountPageButton.setOnClickListener {
            val myAccountPageIntent: Intent = Intent(this@SearchAccountsActivity, MainActivity::class.java)
            startActivity(myAccountPageIntent)
        }
    }
}