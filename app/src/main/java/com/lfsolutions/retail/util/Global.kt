package com.lfsolutions.retail.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan

fun makeTextBold(
    text: String,
    startIndex: Int
): SpannableStringBuilder =
    SpannableStringBuilder(text).let { spannable ->

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable

    }