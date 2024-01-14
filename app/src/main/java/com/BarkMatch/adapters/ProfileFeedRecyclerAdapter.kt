package com.BarkMatch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragment
import com.BarkMatch.models.Post

class ProfileFeedRecyclerAdapter(var posts: List<Post>?) : RecyclerView.Adapter<ProfileFeedViewHolder>() {

    private var listener: ProfileFragment.OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFeedViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_post_layout, parent, false)
        return ProfileFeedViewHolder(itemView, listener, posts)
    }

    override fun onBindViewHolder(holder: ProfileFeedViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post, position)
    }
}