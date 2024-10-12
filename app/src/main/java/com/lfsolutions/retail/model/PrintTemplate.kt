package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class PrintTemplate(
    @SerializedName("name") var name: String? = null,
    @SerializedName("template") var template: String? = null
)