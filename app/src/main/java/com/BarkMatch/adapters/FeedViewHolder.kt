package com.BarkMatch.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.BarkMatch.BreedInfoActivity
import com.BarkMatch.R
import com.BarkMatch.models.Model
import com.BarkMatch.models.Post
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

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

        if (post != null) {
            Model.instance.getUserContactDetails(post.ownerId) { phoneNumber, fullName ->
                tvPostUsername?.text = fullName

                imPostContactInfo?.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle("Contact details")
                    alertDialogBuilder.setMessage("""
                        Name: $fullName
                        
                        Phone number: $phoneNumber
                    """.trimIndent())
                    alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        }
    }
}