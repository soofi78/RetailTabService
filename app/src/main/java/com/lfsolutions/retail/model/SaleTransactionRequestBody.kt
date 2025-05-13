package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class SaleTransactionRequestBody(
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("transactionTypes") var transactionTypes: ArrayList<Int> = arrayListOf(
        10,
        20,
        40,
        50
    )
)