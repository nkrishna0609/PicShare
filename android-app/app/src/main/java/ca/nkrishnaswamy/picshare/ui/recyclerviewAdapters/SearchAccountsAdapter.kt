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
import de.hdodenhof.circleimageview.CircleImageView

class SearchAccountsAdapter internal constructor(val context: Context, private val listener : OnItemClickListener) : RecyclerView.Adapter<SearchAccountsAdapter.SearchAccountsViewHolder>(){

    private var searchedAccountsList = mutableListOf<UserModel>()

    class SearchAccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameVal : TextView = itemView.findViewById(R.id.username)
        private val profilePic : CircleImageView = itemView.findViewById(R.id.profileImage)
        val layout : LinearLayout = itemView.findViewById(R.id.searchedAccountsLayout)

        fun bind(account : UserModel, listener : OnItemClickListener) {
            usernameVal.text = account.username
            profilePic.setImageURI(Uri.parse(account.profilePicPathFromUri))

            itemView.setOnClickListener {
                listener.onItemClick(account)
            }
        }
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
        holder.bind(searchedAccountsList[position], listener)
    }

    internal fun setSearchedAccountsList(accountsList: List<UserModel>) {
        searchedAccountsList = accountsList.toMutableList()
        notifyDataSetChanged()
    }

    fun getAccountAt(position : Int) : UserModel {
        return searchedAccountsList[position]
    }

}