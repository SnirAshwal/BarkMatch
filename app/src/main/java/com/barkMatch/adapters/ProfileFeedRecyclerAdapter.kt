package com.barkMatch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barkMatch.R
import com.barkMatch.models.Post

class ProfileFeedRecyclerAdapter(var posts: MutableList<Post>?) :
    RecyclerView.Adapter<ProfileFeedViewHolder>() {

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFeedViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_post_layout, parent, false)
        return ProfileFeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileFeedViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post)
    }
}