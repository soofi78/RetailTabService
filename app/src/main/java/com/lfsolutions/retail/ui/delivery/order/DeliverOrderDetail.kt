package com.lfsolutions.retail.ui.delivery.order

import com.google.gson.annotations.SerializedName

data class DeliverOrderDetail(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null,
)