package com.videotel.digital.util

import java.math.BigDecimal

object NumberOperations {
    @JvmStatic
    fun round(value: String?, decimalPlaces: Int): String {
        val a = BigDecimal(value)
        val roundOff = a.setScale(decimalPlaces, BigDecimal.ROUND_HALF_EVEN)
        return roundOff.toString()
    }
}