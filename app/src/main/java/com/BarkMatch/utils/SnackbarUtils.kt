package com.BarkMatch.utils

import android.view.View
import com.BarkMatch.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(view.context.getColor(R.color.snackbarBackgroundTint))
            .setActionTextColor(view.context.getColor(R.color.snackbarTextColor)).show()
    }
}