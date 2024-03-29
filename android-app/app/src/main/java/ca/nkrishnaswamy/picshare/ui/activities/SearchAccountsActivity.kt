package ca.nkrishnaswamy.picshare.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost
import ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters.OnItemClickListener
import ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters.SearchAccountsAdapter
import ca.nkrishnaswamy.picshare.viewModels.AuthViewModel
import ca.nkrishnaswamy.picshare.viewModels.SignedInUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAccountsActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var myAccountPageButton: ImageButton
    private lateinit var searchBoxET : EditText
    private lateinit var searchButton : ImageButton
    private lateinit var signedInUserVM : SignedInUserViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var searchedUserList : ArrayList<UserModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profiles)

        val context: Context = this

        signedInUserVM = ViewModelProvider(this).get(SignedInUserViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        val recyclerViewAdapter = SearchAccountsAdapter(this, object: OnItemClickListener {
            override fun onItemClick(account: UserModel) {
                CoroutineScope(Dispatchers.IO).launch{
                    val idToken = authViewModel.getUserIdToken()
                    if (idToken != null) {
                        val postList : ArrayList<UserPost> = signedInUserVM.getPostsForUserSearch(context, idToken, account.email)
                        withContext(Dispatchers.Main) {
                            val intent = Intent(context, ProfileSearchResultActivity::class.java)
                            intent.putExtra("searchedAccount", account)
                            intent.putParcelableArrayListExtra("postList", postList)
                            startActivity(intent)
                        }
                    }
                }
            }
        })

        searchBoxET = findViewById(R.id.searchBox)

        searchButton=findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            val searchQuery: String = searchBoxET.text.toString()

            if (!TextUtils.isEmpty(searchBoxET.text)){
                CoroutineScope(Dispatchers.IO).launch{
                    searchedUserList = signedInUserVM.searchForAccounts(context, searchQuery)
                    withContext(Dispatchers.Main) {
                        recyclerViewAdapter.setSearchedAccountsList(searchedUserList)
                        recyclerView.adapter = recyclerViewAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)

                    }
                }
            }
        }

        myAccountPageButton=findViewById(R.id.myAccountPageButton)
        myAccountPageButton.setOnClickListener {
            val myAccountPageIntent = Intent(this@SearchAccountsActivity, MainActivity::class.java)
            startActivity(myAccountPageIntent)
        }
    }
}