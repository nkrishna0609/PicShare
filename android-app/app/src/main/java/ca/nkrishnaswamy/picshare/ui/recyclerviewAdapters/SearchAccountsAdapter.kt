package ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.ui.activities.ProfileSearchResultActivity
import de.hdodenhof.circleimageview.CircleImageView

class SearchAccountsAdapter internal constructor(val context: Context) : RecyclerView.Adapter<SearchAccountsAdapter.SearchAccountsViewHolder>(){

    private var searchedAccountsList = mutableListOf<UserModel>()

    class SearchAccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameVal : TextView = itemView.findViewById(R.id.username)
        val profilePic : CircleImageView = itemView.findViewById(R.id.profileImage)
        val layout : LinearLayout = itemView.findViewById(R.id.searchedAccountsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAccountsViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.searched_account_row, parent, false)
        return SearchAccountsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchedAccountsList.size
    }

    override fun onBindViewHolder(holder: SearchAccountsViewHolder, position: Int) {
        holder.usernameVal.text = searchedAccountsList[position].username
        holder.profilePic.setImageURI(Uri.parse(searchedAccountsList[position].profilePicPathFromUri))

        holder.layout.setOnClickListener {
            val intent = Intent(context, ProfileSearchResultActivity::class.java)
            intent.putExtra("searchedAccount", searchedAccountsList[position])
            context.startActivity(intent)
        }
    }

    internal fun setSearchedAccountsList(accountsList: List<UserModel>) {
        searchedAccountsList = accountsList.toMutableList()
        notifyDataSetChanged()
    }

    fun getAccountAt(position : Int) : UserModel {
        return searchedAccountsList[position]
    }

}