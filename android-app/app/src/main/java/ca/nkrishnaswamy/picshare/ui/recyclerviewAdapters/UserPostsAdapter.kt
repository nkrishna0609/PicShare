package ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import ca.nkrishnaswamy.picshare.R
import ca.nkrishnaswamy.picshare.data.models.roomModels.UserPost
import ca.nkrishnaswamy.picshare.ui.activities.UserPostViewActivity

class UserPostsAdapter internal constructor(val context: Context) : RecyclerView.Adapter<UserPostsAdapter.UserPostsViewHolder>() {

    private var postList = mutableListOf<UserPost>()

    class UserPostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImage)
        val linearLayoutPost : LinearLayout = itemView.findViewById(R.id.postLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostsViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.user_post_grid_cell, parent, false)
        return UserPostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPostsViewHolder, position: Int) {
        val uriPath : String = postList[position].uriImgPathString
        holder.postImageView.setImageURI(Uri.parse(uriPath))
        holder.linearLayoutPost.setOnClickListener {
            val intent = Intent(context, UserPostViewActivity::class.java)
            intent.putExtra("post", postList[position])
            context.startActivity(intent)
        }
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