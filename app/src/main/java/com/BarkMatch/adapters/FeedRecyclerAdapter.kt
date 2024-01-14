package com.BarkMatch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragment
import com.BarkMatch.models.Post

class FeedRecyclerAdapter(
    var posts: List<Post>?,
    private val context: Context
) : RecyclerView.Adapter<FeedViewHolder>() {

    var listener: ProfileFragment.OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return FeedViewHolder(itemView, context, posts)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post)
    }
}