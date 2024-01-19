package com.BarkMatch.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

object ImagesUtils {

    fun loadImage(imageUrl: String, imageView: ImageView) {
        Picasso.get()
            .load(imageUrl)
            .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
            .fit()
            .centerCrop()
            .into(imageView)
    }
}