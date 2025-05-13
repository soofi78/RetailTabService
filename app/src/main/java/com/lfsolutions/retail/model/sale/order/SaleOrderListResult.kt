package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName


data class SaleOrderListResult(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<SaleOrderListItem> = arrayListOf()
)