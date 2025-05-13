package com.lfsolutions.retail.ui.saleorder

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null
)