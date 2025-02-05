package com.lfsolutions.retail.ui.taxinvoice

import com.google.gson.annotations.SerializedName

data class Invoice(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null
)