package com.BarkMatch.adapters

import android.view.View
import android.widget.ImageView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragmentDirections
import com.BarkMatch.models.Post
import com.BarkMatch.utils.ImagesUtils
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class ProfileFeedViewHolder(
    itemView: View,
    var posts: List<Post>?
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