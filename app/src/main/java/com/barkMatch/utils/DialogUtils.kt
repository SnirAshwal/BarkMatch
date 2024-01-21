package com.barkMatch.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {

    fun openDialog(context: Context, title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun openDialog(
        context: Context,
        title: String,
        message: String,
        buttonActions: Array<ButtonAction>
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        addButtons(alertDialogBuilder, buttonActions)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun addButtons(builder: AlertDialog.Builder, buttonActions: Array<ButtonAction>) {
        for (buttonAction in buttonActions) {
            when (buttonAction.type) {
                ButtonType.POSITIVE -> builder.setPositiveButton(buttonAction.text) { dialog, _ ->
                    buttonAction.action.invoke(dialog as AlertDialog)
                }
                ButtonType.NEGATIVE -> builder.setNegativeButton(buttonAction.text) { dialog, _ ->
                    buttonAction.action.invoke(dialog as AlertDialog)
                }
                ButtonType.NEUTRAL -> builder.setNeutralButton(buttonAction.text) { dialog, _ ->
                    buttonAction.action.invoke(dialog as AlertDialog)
                }
            }
        }
    }

    data class ButtonAction(val text: String, val type: ButtonType, val action: (dialog: AlertDialog) -> Unit)

    enum class ButtonType {
        POSITIVE, NEGATIVE, NEUTRAL
    }
}
