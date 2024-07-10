package com.lfsolutions.retail.network.serializers

import com.google.gson.annotations.SerializedName


data class Error(
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("details") var details: String? = null,
    @SerializedName("validationErrors") var validationErrors: String? = null
)