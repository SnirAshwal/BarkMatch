package com.BarkMatch.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.BreedInfoActivity
import com.BarkMatch.R
import com.BarkMatch.models.Post
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class FeedViewHolder(
    itemView: View,
//    val listener: StudentsRcyclerViewActivity.OnItemClickListener?,
    private val context: Context,
    var posts: List<Post>?
) : RecyclerView.ViewHolder(itemView) {

    private var tvPostDescription: TextView? = null
    private var ivPostImage: ImageView? = null
    private var imPostBreedInfo: ImageView? = null
    private var tvPostBreed: TextView? = null
    private var tvPostUsername: TextView? = null

    var post: Post? = null

    init {
        tvPostDescription = itemView.findViewById(R.id.tvPostDescription)
        ivPostImage = itemView.findViewById(R.id.ivPostImage)
        imPostBreedInfo = itemView.findViewById(R.id.imPostBreedInfo)
        tvPostBreed = itemView.findViewById(R.id.tvPostBreed)
        tvPostUsername = itemView.findViewById(R.id.tvPostUsername)
    }

    fun bind(post: Post?) {
        this.post = post
        tvPostDescription?.text = post?.description
        tvPostBreed?.text = post?.breedName
        tvPostUsername?.text = "username"

        if (post?.image?.isEmpty() == false) {
            // Loading post img
            Picasso.get()
                .load(post.image)
                .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
                .fit()
                .centerCrop()
                .into(ivPostImage)
        }

        imPostBreedInfo?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("breedId", post?.breedId.toString())
            val intent = Intent(context, BreedInfoActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}