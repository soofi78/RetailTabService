package com.lfsolutions.retail.ui.stocktransfer.incoming

import com.google.gson.annotations.SerializedName

data class StockReceived(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null
)