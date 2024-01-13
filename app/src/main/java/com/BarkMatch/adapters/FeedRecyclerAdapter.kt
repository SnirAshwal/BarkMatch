package com.BarkMatch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.models.Post

class FeedRecyclerAdapter (var posts: List<Post>?): RecyclerView.Adapter<FeedViewHolder>() {

//    var listener: FeedFragment.OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return FeedViewHolder(itemView, posts)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val student = posts?.get(position)
        holder.bind(student)
    }
}