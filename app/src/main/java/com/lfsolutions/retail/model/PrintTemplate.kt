package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class PrintTemplate(
    @SerializedName("name") var name: String? = null,
    @SerializedName("printDefault", alternate = arrayOf("PrintDefault")) var printDefault: Int = 1,
    @SerializedName("template") var template: String? = null
)