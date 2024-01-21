package com.barkMatch.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

object ImagesUtils {

    fun loadImage(imageUrl: String, imageView: ImageView) {
        if (imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
                .fit()
                .centerCrop()
                .into(imageView)
        } else {
            Log.i("Tag", "imageUrl is empty")
        }
    }

    fun loadImage(uri: Uri?, imageView: ImageView) {
        if (uri != null) {
            Picasso.get()
                .load(uri)
                .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
                .fit()
                .centerCrop()
                .into(imageView)
        } else {
            Log.i("Tag", "Uri is null")
        }
    }
}