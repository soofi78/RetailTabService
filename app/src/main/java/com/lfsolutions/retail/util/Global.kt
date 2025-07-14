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
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.round


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
            if (applyDecimalSettings) Main.app.getSession().decimalDigitsForPriceInSalesModule?.takeIf { it>0 }?:2 else 2
        fmt.maximumFractionDigits =
            if (applyDecimalSettings) Main.app.getSession().decimalDigitsForPriceInSalesModule?.takeIf { it>0 }?:2 else 2

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


fun Double.getRoundOffValue(dRounding: BigDecimal, roundDown: Boolean): Double {
    println("startingTotal:$this, dRounding:$dRounding, roundDown:$roundDown")

    if (dRounding.compareTo(BigDecimal.ZERO) == 0) {
        return this
    }

    val total = this.toBigDecimal()

    val rounded = if (roundDown) {//true
        total.divide(dRounding, 0, RoundingMode.FLOOR).multiply(dRounding)
    } else {
        total.divide(dRounding, 0, RoundingMode.CEILING).multiply(dRounding)
    }

    return rounded.toDouble()
}


fun getRoundOffValue(totalPrice: Double, roundOff: Double, roundDown: Boolean): Pair<Double,Double> {
    return when (roundDown) {
        false -> { //when values should be round up
            getRoundUpValue(totalPrice, roundOff) //Always Round +
        }
        true -> { //when values should be round down
            getRoundDownValue(totalPrice, roundOff) //Always Round -
        }
    }
}


private fun getRoundUpValue(totalPrice: Double, roundOff: Double): Pair<Double,Double>  {
    val roundedPrice = if (roundOff > 0) {
        println("totalPrice: $totalPrice")
        val rounded = (totalPrice / roundOff).toInt() * roundOff
        println("rounded: $rounded")
        val remainder = totalPrice - rounded
        println("remainder: $remainder")
        if (remainder > 0) {
            println("rounded: ${rounded + roundOff}")
            rounded + roundOff
        } else {
            rounded
        }
    } else {
        totalPrice
    }
    val finalRoundUpValue=(roundedPrice-totalPrice).times(1)
    println("roundedPrice: $roundedPrice|finalRoundUpValue: $finalRoundUpValue")
    return Pair(roundedPrice,finalRoundUpValue)
}

private fun getRoundDownValue(totalPrice: Double, roundOff: Double): Pair<Double,Double> {
    println("totalPrice:$totalPrice,roundOff:$roundOff")
    var finalRoundDownValue=0.0
    val roundedPrice = if (roundOff > 0) {
        val epsilon = 0.00001 // Adjust epsilon as needed
        val rounded = floor((totalPrice + epsilon) / roundOff) * roundOff
        rounded
    } else {
        totalPrice
    }
    finalRoundDownValue=(totalPrice-roundedPrice).times(-1)
    return Pair(roundedPrice,finalRoundDownValue)
}

private fun getRoundOffDefaultValue(totalPrice: Double, roundOff: Double): Double {
    val roundedPrice = if (roundOff > 0) {
        val epsilon = 0.00001 // Adjust epsilon as needed
        val rounded = round((totalPrice + epsilon) / roundOff) * roundOff
        rounded
    } else {
        totalPrice
    }
    return roundedPrice
}

fun Double.formatPriceForApi(applyDecimalSettings: Boolean = false): Double {
    val decimals = if(applyDecimalSettings)Main.app.getSession().decimalDigitsForPriceInSalesModule?.takeIf { it > 0 } ?: 2 else 2
    return this.toBigDecimal()
        .setScale(decimals, RoundingMode.HALF_UP)
        .toDouble()
}




