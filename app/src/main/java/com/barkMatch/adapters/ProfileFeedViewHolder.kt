package com.barkMatch.adapters

import android.view.View
import android.widget.ImageView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.barkMatch.R
import com.barkMatch.homePageFragments.ProfileFragmentDirections
import com.barkMatch.models.Post
import com.barkMatch.utils.ImagesUtils

class ProfileFeedViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private var ivProfilePostImage: ImageView? = null
    var post: Post? = null

    init {
        ivProfilePostImage = itemView.findViewById(R.id.ivProfilePostImage)
    }

    fun bind(post: Post?) {
        this.post = post
        ivProfilePostImage?.let { ImagesUtils.loadImage(post?.image ?: "", it) }

        ivProfilePostImage?.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileToProfilePost(post?.id)
            findNavController(itemView).navigate(action)
        }
    }
}