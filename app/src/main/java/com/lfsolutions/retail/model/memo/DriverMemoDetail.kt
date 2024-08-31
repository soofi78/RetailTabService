package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class DriverMemoDetail(

    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("productImage") var productImage: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("qty") var qty: Double = 0.0,
    @SerializedName("cost") var cost: Double? = null,
    @SerializedName("totalCost") var totalCost: Double? = null,
    @SerializedName("price") var price: Double = 0.0,
    @SerializedName("totalPrice") var totalPrice: Double = 0.0,
    @SerializedName("uom") var uom: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("driverType") var driverType: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("changeunitflag") var changeunitflag: Boolean? = null

) {
    fun updateTotal() {
        totalPrice = qty * price
    }
}