package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class StockTransferRequestBody(
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("stockTransferDetails") var stockTransferDetails: ArrayList<OutGoingProduct> = arrayListOf()
)