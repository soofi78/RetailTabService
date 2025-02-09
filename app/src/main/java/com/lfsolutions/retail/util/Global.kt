package com.lfsolutions.retail.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale


fun makeTextBold(
    text: String, startIndex: Int
): SpannableStringBuilder = SpannableStringBuilder(text).let { spannable ->

    spannable.setSpan(
        StyleSpan(Typeface.BOLD), startIndex, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
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
    if (this == null) return "0"
    else {
        val fmt = NumberFormat.getCurrencyInstance(Locale.getDefault())
        fmt.maximumFractionDigits = 2
        fmt.roundingMode = RoundingMode.HALF_UP
        return fmt.format(this.toDouble()).replace(fmt.currency.symbol, "")
    }
}

fun Int.formatDecimalSeparator(): String {
    return this.toString().formatDecimalSeparator()
}

fun Double.formatDecimalSeparator(): String {
    return this.toString().formatDecimalSeparator()
}

fun View.setDebouncedClickListener(onClick: (View) -> Unit) {
    setOnClickListener {
        if (ClickDebouncer.canClick()) {
            onClick(it)
        }
    }
}