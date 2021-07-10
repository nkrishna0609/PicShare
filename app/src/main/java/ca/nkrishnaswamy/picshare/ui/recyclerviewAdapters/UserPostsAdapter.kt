package ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.UserPost

class UserPostsAdapter internal constructor(val context: Context) : RecyclerView.Adapter<UserPostsAdapter.UserPostsViewHolder>() {

    private var postList = mutableListOf<UserPost>()

    class UserPostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostsViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.user_post_grid_cell, parent, false)
        return UserPostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPostsViewHolder, position: Int) {
        val uriPath : String = postList[position].getUriImgPathString()
        holder.postImageView.setImageURI(Uri.parse(uriPath))
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    internal fun setPostList(userPostList: List<UserPost>) {
        postList = userPostList.toMutableList()
        notifyDataSetChanged()
    }

    fun getPostAt(position : Int) : UserPost {
        return postList[position]
    }
}