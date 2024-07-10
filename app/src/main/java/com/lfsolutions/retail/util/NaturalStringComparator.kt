package com.lfsolutions.retail.util

/**
 * Created by devandro on 11/30/16.
 */
class NaturalStringComparator : Comparator<Any?> {
    fun compareRight(a: String, b: String): Int {
        var bias = 0
        var ia = 0
        var ib = 0

        // The longest run of digits wins. That aside, the greatest
        // value wins, but we can't know that it will until we've scanned
        // both numbers to know that they have the same magnitude, so we
        // remember it in BIAS.
        while (true) {
            val ca = charAt(a, ia)
            val cb = charAt(b, ib)
            if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                return bias
            }
            if (!Character.isDigit(ca)) {
                return -1
            }
            if (!Character.isDigit(cb)) {
                return +1
            }
            if (ca.code == 0 && cb.code == 0) {
                return bias
            }
            if (bias == 0) {
                if (ca < cb) {
                    bias = -1
                } else if (ca > cb) {
                    bias = +1
                }
            }
            ia++
            ib++
        }
    }

    override fun compare(o1: Any?, o2: Any?): Int {
        val a = o1.toString()
        val b = o2.toString()
        var ia = 0
        var ib = 0
        var nza = 0
        var nzb = 0
        var ca: Char
        var cb: Char
        while (true) {
            // Only count the number of zeroes leading the last number compared
            nzb = 0
            nza = nzb
            ca = charAt(a, ia)
            cb = charAt(b, ib)

            // skip over leading spaces or zeros
            while (Character.isSpaceChar(ca) || ca == '0') {
                if (ca == '0') {
                    nza++
                } else {
                    // Only count consecutive zeroes
                    nza = 0
                }
                ca = charAt(a, ++ia)
            }
            while (Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    nzb++
                } else {
                    // Only count consecutive zeroes
                    nzb = 0
                }
                cb = charAt(b, ++ib)
            }

            // Process run of digits
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                val bias = compareRight(a.substring(ia), b.substring(ib))
                if (bias != 0) {
                    return bias
                }
            }
            if (ca.code == 0 && cb.code == 0) {
                // The strings compare the same. Perhaps the caller
                // will want to call strcmp to break the tie.
                return nza - nzb
            }
            if (ca < cb) {
                return -1
            }
            if (ca > cb) {
                return +1
            }
            ++ia
            ++ib
        }
    }

    companion object {
        private var naturalStringComparator: NaturalStringComparator? = null

        @JvmStatic
        @get:Synchronized
        val instance: NaturalStringComparator
            get() {
                if (naturalStringComparator == null) naturalStringComparator =
                    NaturalStringComparator()
                return naturalStringComparator!!
            }

        fun charAt(s: String, i: Int): Char {
            return if (i >= s.length) 0.toChar() else s[i]
        }
    }
}