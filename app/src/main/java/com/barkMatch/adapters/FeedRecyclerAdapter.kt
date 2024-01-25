package com.barkMatch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barkMatch.R
import com.barkMatch.models.Post
import com.google.firebase.firestore.DocumentSnapshot

class FeedRecyclerAdapter(
    var posts: List<Post>?,
    private val context: Context
) : RecyclerView.Adapter<FeedViewHolder>() {

    companion object {
        const val FEED_PAGE_SIZE = 8L
        var isLoading = false
        var isLastPage = false
        var lastVisiblePost: DocumentSnapshot? = null
    }

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