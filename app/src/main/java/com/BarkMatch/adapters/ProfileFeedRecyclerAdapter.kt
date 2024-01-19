package com.BarkMatch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragment
import com.BarkMatch.models.Post
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFeedRecyclerAdapter(var posts: MutableList<Post>?) :
    RecyclerView.Adapter<ProfileFeedViewHolder>() {

    companion object {
        const val PROFILE_PAGE_SIZE = 10L
        var isLoading = false
        var isLastPage = false
        var lastVisiblePost: DocumentSnapshot? = null
    }

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFeedViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_post_layout, parent, false)
        return ProfileFeedViewHolder(itemView, posts)
    }

    override fun onBindViewHolder(holder: ProfileFeedViewHolder, position: Int) {
        val post = posts?.get(position)
        holder.bind(post)
    }
}