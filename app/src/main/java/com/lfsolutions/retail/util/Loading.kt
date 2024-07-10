package com.lfsolutions.retail.util

import android.app.Activity
import android.app.ProgressDialog
import com.lfsolutions.retail.util.Logger

class Loading {
    private var pDialog: ProgressDialog? = null
    private var activity: Activity? = null


    fun forApi(context: Activity): Loading {
        show(context, false, "Please wait...")
        return this
    }

    fun show(context: Activity, cancelable: Boolean, message: String?): ProgressDialog {
        if (pDialog == null) {
            pDialog = ProgressDialog(context)
            activity = context
            try {
                context.runOnUiThread {
                    pDialog!!.setCancelable(cancelable)
                    pDialog!!.setMessage(message)
                    pDialog!!.show()
                }
            } catch (e: Exception) {
                Logger.log(Logger.EXCEPTION, e)
                Logger.log("loading show")
            }
        } else {
            Logger.log("loading already shown")
        }
        return pDialog!!
    }

    fun cancel() {
        try {
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.cancel()
                pDialog = null
            }
        } catch (e: Exception) {
            Logger.log("cancel")
        }
    }

    fun updateMessage(message: String?) {
        activity!!.runOnUiThread {
            if (pDialog != null && pDialog!!.isShowing) pDialog!!.setMessage(
                message
            )
        }
    }

    val isVisible: Boolean
        get() = pDialog != null && pDialog!!.isShowing
}