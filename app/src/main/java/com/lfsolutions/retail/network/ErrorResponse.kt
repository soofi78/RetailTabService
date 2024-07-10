package com.lfsolutions.retail.network

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.network.serializers.Error


data class ErrorResponse(

    @SerializedName("result") var result: String? = null,
    @SerializedName("targetUrl") var targetUrl: String? = null,
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("error") var error: Error? = Error(),
    @SerializedName("unAuthorizedRequest") var unAuthorizedRequest: Boolean? = null,
    @SerializedName("__abp") var _abp: Boolean? = null

)