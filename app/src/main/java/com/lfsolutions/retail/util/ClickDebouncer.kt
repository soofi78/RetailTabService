package com.lfsolutions.retail.util

object ClickDebouncer {
    private var lastClickTime: Long = 0
    private const val DEBOUNCE_DELAY = 500L // Adjust delay as needed

    fun canClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < DEBOUNCE_DELAY) {
            return false
        }
        lastClickTime = currentTime
        return true
    }
}
