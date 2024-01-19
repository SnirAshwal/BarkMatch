package com.BarkMatch.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {

    fun openDialog(context: Context, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Contact details")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
