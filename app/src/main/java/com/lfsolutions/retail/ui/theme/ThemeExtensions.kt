package com.lfsolutions.retail.ui.theme


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun Activity.recreateSmoothly() {
    startActivity(Intent(this, this::class.java))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    finish()
}

@ColorInt
fun Context.getAppColor(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(this, colorAttr)
    return if (resolvedAttr.resourceId != 0) {
        return ContextCompat.getColor(this, resolvedAttr.resourceId)
    } else {
        resolvedAttr.data
    }
}

@ColorRes
fun Context.getAppColorRes(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(this, colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    return if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else 0
}

private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}

