package com.BarkMatch.adapters

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragment
import com.BarkMatch.models.Post
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class ProfileFeedViewHolder(
    itemView: View,
    private val listener: ProfileFragment.OnItemClickListener?,
    var posts: List<Post>?
) : RecyclerView.ViewHolder(itemView) {

    private var ivProfilePostImage: ImageView? = null
    var post: Post? = null

    init {
        ivProfilePostImage = itemView.findViewById(R.id.ivProfilePostImage)

        itemView.setOnClickListener {
            listener?.onItemClick(adapterPosition)
            listener?.onPostClicked(post)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        Picasso.get()
            .load(post?.image)
            .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
            .fit()
            .centerCrop()
            .into(ivProfilePostImage)
    }
}