package com.BarkMatch.adapters

import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.R
import com.BarkMatch.homePageFragments.ProfileFragment
import com.BarkMatch.models.Post
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

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

    fun bind(post: Post?, position: Int) {
        this.post = post

            val imageFile = File(
                itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "dog_post_img.jpg"
            )

            // Loading post 1 img
            Picasso.get()
                .load(R.drawable.dog_post_img)
                .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
                .into(ivProfilePostImage)
        }
}