package ca.nkrishnaswamy.picshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    lateinit var searchPageButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchPageButton=findViewById(R.id.searchPageButton)
        searchPageButton.setOnClickListener {
            val searchPageIntent: Intent = Intent(this@MainActivity, SearchAccountsActivity::class.java)
            startActivity(searchPageIntent)
        }
    }
}