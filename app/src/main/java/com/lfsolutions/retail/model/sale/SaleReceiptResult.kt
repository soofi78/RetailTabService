package com.lfsolutions.retail.model.sale

import com.google.gson.annotations.SerializedName


data class SaleReceiptResult(

    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<SaleReceipt> = arrayListOf()

)