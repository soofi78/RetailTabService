package com.lfsolutions.retail.network

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.network.serializers.Error


data class BaseResponse<T>(

    @SerializedName("result") var result: T? = null,
    @SerializedName("targetUrl") var targetUrl: String? = null,
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("error") var error: Error? = Error(),
    @SerializedName("unAuthorizedRequest") var unAuthorizedRequest: Boolean? = null,
    @SerializedName("__abp") var _abp: Boolean? = null

) {
    fun getMessage(message: String): String {
        if (error != null && error?.message.isNullOrEmpty().not()) {
            return error!!.message!!
        }
        return message
    }
}