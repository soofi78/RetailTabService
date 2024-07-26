package com.lfsolutions.retail.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.videotel.digital.util.DateTime

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


fun String?.formatToDate(): String? {
    return DateTime.format(
        DateTime.getDateFromString(this, DateTime.DateTimeRetailFrontEndFormate),
        DateTime.DateFormatRetail
    )
}

fun String.formatDecimalSeparator(): String {
    return this.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

fun Int.formatDecimalSeparator(): String {
    return this.toString().formatDecimalSeparator()
}

fun Double.formatDecimalSeparator(): String {
    return this.toString().formatDecimalSeparator()
}