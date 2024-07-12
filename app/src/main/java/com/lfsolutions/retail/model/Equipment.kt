package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class Equipment(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null
)