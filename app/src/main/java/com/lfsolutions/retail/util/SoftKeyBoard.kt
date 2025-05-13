package com.videotel.digital.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by faheem on 3/5/18.
 */
object SoftKeyBoard {
    fun hide(mContext: Activity) {
        try {
            val inputMethodManager =
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mContext.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    fun show(mContext: Activity, et: EditText?) {
        try {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
        }
    }
}