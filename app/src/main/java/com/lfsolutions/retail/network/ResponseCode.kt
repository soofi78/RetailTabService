package com.lfsolutions.retail.network

/**
 * Created by devandro on 11/20/17.
 */
object ResponseCode {
    var SUCCESS: Int = 200
    var INTERNAL_SERVER_ERROR: Int = 500
    var FORBIDDEN: Int = 403
    var UN_AUTHORIZED: Int = 401
    var ERROR: Int = 422

    fun isBetweenSuccessRange(reqCode: Int): Boolean {
        return try {
            reqCode > 199 && reqCode < 300
        } catch (e: Exception) {
            true
        }
    }
}
