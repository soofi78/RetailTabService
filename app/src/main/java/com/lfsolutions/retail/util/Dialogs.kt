package com.lfsolutions.retail.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener

object Dialogs {
    fun optionsDialog(
        name: String = "Please select",
        context: Activity,
        options: Array<CharSequence>,
        onOptionDialogItemClicked: OnOptionDialogItemClicked
    ) {
        val builder = AlertDialog.Builder(context);
        builder.setTitle("Pick a color");
        builder.setItems(options) { dialog, which ->
            onOptionDialogItemClicked.onClick(options.get(which).toString())
            dialog.dismiss()
        }
        builder.show();
    }


}

interface OnOptionDialogItemClicked {
    fun onClick(option: String)
}