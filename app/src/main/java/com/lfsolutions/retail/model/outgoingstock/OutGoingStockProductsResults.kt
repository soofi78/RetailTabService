package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class OutGoingStockProductsResults(
    @SerializedName("items") var items: ArrayList<OutGoingProduct> = arrayListOf()
)