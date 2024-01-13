package com.BarkMatch.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.models.Post

class FeedViewHolder (
    itemView: View,
//    val listener: StudentsRcyclerViewActivity.OnItemClickListener?,
    var posts: List<Post>?): RecyclerView.ViewHolder(itemView) {

    private var nameTextView: TextView? = null
    var post: Post? = null

    init {
        nameTextView = itemView.findViewById(R.id.tvPostDescription)
    }

    fun bind(post: Post?) {
        this.post = post
        nameTextView?.text = post?.description
    }
}