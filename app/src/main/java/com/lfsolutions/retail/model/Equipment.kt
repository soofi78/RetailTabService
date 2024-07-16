package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class Equipment(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Long? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int? = null,
    @SerializedName("cost") var cost: Int? = null,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null,
    @SerializedName("type") var type: String? = null
) {
    fun isSerialEquipment(): Boolean {
        return type.equals("S", true)
    }
}