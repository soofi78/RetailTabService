package com.lfsolutions.retail.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.R


class BaseActivity : AppCompatActivity() {

    private var customDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun showCustomDialog() {

        if (customDialog == null) {

            customDialog = Dialog(this)

            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

            customDialog?.setContentView(R.layout.custom_loading_dialog)

        }

        if (customDialog?.isShowing != true)
            customDialog?.show()

    }

    protected fun hideCustomDialog() {
        customDialog?.dismiss()
    }

}