package com.barkMatch.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barkMatch.BreedInfoActivity
import com.barkMatch.R
import com.barkMatch.models.Model
import com.barkMatch.models.Post
import com.barkMatch.utils.DialogUtils
import com.barkMatch.utils.ImagesUtils

class FeedViewHolder(
    itemView: View,
    private val context: Context,
    var posts: List<Post>?
) : RecyclerView.ViewHolder(itemView) {

    private var tvPostDescription: TextView? = null
    private var ivPostImage: ImageView? = null
    private var imPostBreedInfo: ImageView? = null
    private var tvPostBreed: TextView? = null
    private var tvPostUsername: TextView? = null
    private var imPostContactInfo: ImageView? = null

    var post: Post? = null

    init {
        tvPostDescription = itemView.findViewById(R.id.tvPostDescription)
        ivPostImage = itemView.findViewById(R.id.ivPostImage)
        imPostBreedInfo = itemView.findViewById(R.id.imPostBreedInfo)
        tvPostBreed = itemView.findViewById(R.id.tvPostBreed)
        tvPostUsername = itemView.findViewById(R.id.tvPostUsername)
        imPostContactInfo = itemView.findViewById(R.id.imPostContactInfo)
    }

    fun bind(post: Post?) {
        this.post = post
        tvPostDescription?.text = post?.description
        tvPostBreed?.text = post?.breedName

        if (post?.image?.isEmpty() == false) {
            // Loading post img
            ivPostImage?.let { ImagesUtils.loadImage(post.image, it) }
        }

        imPostBreedInfo?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("breedId", post?.breedId.toString())
            val intent = Intent(context, BreedInfoActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        if (post != null) {
            Model.instance.getUserContactDetails(post.ownerId) { phoneNumber, fullName ->
                tvPostUsername?.text = fullName

                imPostContactInfo?.setOnClickListener {
                    DialogUtils.openDialog(
                        context, "Contact details", """
                        Name: $fullName
                        
                        Phone number: $phoneNumber
                    """.trimIndent()
                    )
                }
            }
        }
    }
}