package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class UserIdDateRequestBody(
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("date") var date: String? = null
)