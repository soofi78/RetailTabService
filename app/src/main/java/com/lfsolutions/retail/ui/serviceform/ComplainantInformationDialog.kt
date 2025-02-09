package com.lfsolutions.retail.ui.serviceform

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ComplainantInformationDialogBinding
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.SoftKeyBoard

/**
 * Created by devandro on 11/7/17.
 */
class ComplainantInformationDialog(val activity: Activity?) {
    private var onConfirmListener: OnConfirmListener? = null
    var alertBuilder: AlertDialog.Builder? = null
        private set
    var dialog: AlertDialog? = null
        private set
    private var binding: ComplainantInformationDialogBinding? = null


    private fun init(
        name: String?,
        designation: String?,
        mobileNumber: String?
    ): ComplainantInformationDialog {
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ComplainantInformationDialogBinding.inflate(inflater)
        binding!!.complainantName.setText(name)
        binding!!.designation.setText(designation)
        binding!!.mobileNumber.setText(mobileNumber)
        binding!!.cancel.setDebouncedClickListener { dismiss() }
        binding!!.confirm.setDebouncedClickListener {
            SoftKeyBoard.hide(activity)
            onConfirmListener?.onConfirm(
                binding!!.complainantName.text.toString(),
                binding!!.designation.text.toString(),
                binding!!.mobileNumber.text.toString()
            )
            dismiss()
        }
        return this
    }

    fun show(onConfirmListener: OnConfirmListener): AlertDialog? {
        this.onConfirmListener = onConfirmListener
        try {
            if (activity != null) {
                alertBuilder = AlertDialog.Builder(activity)
                alertBuilder!!.setView(binding!!.root)
                alertBuilder!!.setCancelable(false)
                dialog = alertBuilder!!.create()
                dialog!!.window!!.attributes.windowAnimations = R.style.CustomAnimations
                dialog!!.show()
                return dialog
            } else {
                Toast.makeText(Main.app, "Unable to open the dialog", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }


    interface OnConfirmListener {
        fun onConfirm(name: String, designation: String, mobileNumber: String)
    }

    companion object {
        fun make(
            activity: Activity?,
            name: String? = "",
            designation: String? = "",
            mobileNumber: String? = ""
        ): ComplainantInformationDialog {
            val alert = ComplainantInformationDialog(activity)
            return alert.init(name, designation, mobileNumber)
        }
    }
}
