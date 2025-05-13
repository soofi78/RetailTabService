package com.videotel.digital.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.lfsolutions.retail.Main

/**
 * Created by devandro on 7/18/17.
 */
object Notify {

    fun toast(message: String?) {
        Toast.makeText(
            Main.app,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun toastLong(message: String?) {
        Toast.makeText(
            Main.app,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}