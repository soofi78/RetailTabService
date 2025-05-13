package com.lfsolutions.retail.util

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

/**
 * Created by devandro on 11/9/16.
 */
object Logger {
    const val EXCEPTION = "EXCEPTION"
    private val DELIMITER = Pattern.compile("XYZ.123")
    fun log(TAG: String?, MSG: String?) {
        if (TAG != null && MSG != null) {
            Log.d(TAG, MSG)
        }
    }

    fun log(MSG: String?) {
        if (MSG != null) {
            Log.d("Retail", MSG)
        }
    }

    fun log(TAG: String?, e: Exception) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        log(TAG, sw.toString())
    }

    fun log(TAG: String?, e: Throwable) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        log(TAG, sw.toString())
    }

    fun readException(e: Exception?): String {
        return if (e != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            sw.toString()
        } else {
            "Error not found"
        }
    }

    fun readException(e: Throwable?): String {
        return if (e != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            sw.toString()
        } else {
            "Error Not Found"
        }
    }
}