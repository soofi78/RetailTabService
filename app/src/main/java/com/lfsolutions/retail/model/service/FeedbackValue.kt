package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class FeedbackValue(

    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("active") var active: String? = null

)