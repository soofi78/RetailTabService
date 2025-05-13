package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class StockTransferHistoryResult(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<StockTransfer> = arrayListOf()

)