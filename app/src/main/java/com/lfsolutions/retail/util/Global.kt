package com.lfsolutions.retail.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.AppCompatTextView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
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

fun String.formatDecimalSeparator(applyDecimalSettings: Boolean = false): String {
    if (this == null) return "0"
    else {
        val fmt = NumberFormat.getCurrencyInstance(Locale.getDefault())
        fmt.minimumFractionDigits =
            if (applyDecimalSettings) Main.app.getSession().decimalDigitsForPriceInSalesModule
                ?: 2 else 2
        fmt.maximumFractionDigits =
            if (applyDecimalSettings) Main.app.getSession().decimalDigitsForPriceInSalesModule
                ?: 2 else 2
        fmt.roundingMode = RoundingMode.HALF_UP
        return fmt.format(this.toDouble()).replace(fmt.currency.symbol, "")
    }
}

fun Int.formatDecimalSeparator(applyDecimalSettings: Boolean = false): String {
    return this.toString().formatDecimalSeparator(applyDecimalSettings)
}

fun Double.formatDecimalSeparator(applyDecimalSettings: Boolean = false): String {
    return this.toString().formatDecimalSeparator(applyDecimalSettings)
}

fun View.setDebouncedClickListener(onClick: (View) -> Unit) {
    setOnClickListener {
        if (ClickDebouncer.canClick()) {
            onClick(it)
        }
    }
}

fun <T> List<T>.disableQtyFields(
    qtyView: View,
    subButton: View,
    addButton: View,
    disableBgRes: Int = R.drawable.oval_disable_bg
) {
    if (this.isNotEmpty()) {
        qtyView.apply {
            isEnabled = false
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
        }
        subButton.apply {
            isEnabled = false
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            setBackgroundResource(disableBgRes)
        }
        addButton.apply {
            isEnabled = false
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            setBackgroundResource(disableBgRes)
        }
    }
}

fun <T> List<T>.disableQtyBox(
    qtyView: View,
    itemView: View,
    disableBgRes: Int = R.drawable.round_disable_stroke
) {
    if (this.isNotEmpty()) {
        itemView.apply {
            isEnabled=false
            isClickable=false
            isFocusable=false
            isFocusableInTouchMode = false
        }
        qtyView.apply {
            isEnabled = false
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            setBackgroundResource(disableBgRes)
        }
    }
}

fun List<ProductBatchList>.toSerialNumberAdapter(): MultiSelectListAdapter {
    val serialItems = this.map { batch ->
        object : MultiSelectModelInterface {
            override fun getText(): String = batch.SerialNumber.toString()
            override fun setSelected(selected: Boolean) {
                // Optional: store state if needed
            }
        }
    }
    return MultiSelectListAdapter(ArrayList(serialItems))

}

fun View.toAddVisibility(batchList:List<ProductBatchList>){
    visibility = if (batchList.isNotEmpty()) View.VISIBLE else View.GONE
}


