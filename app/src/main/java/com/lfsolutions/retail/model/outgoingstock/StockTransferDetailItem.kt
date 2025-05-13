package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class StockTransferDetailItem(
    @SerializedName("stockTransfer") var stockTransfer: StockTransfer? = StockTransfer(),
    @SerializedName("stockTransferDetail") var stockTransferDetail: ArrayList<StockTransferProduct> = arrayListOf()
)