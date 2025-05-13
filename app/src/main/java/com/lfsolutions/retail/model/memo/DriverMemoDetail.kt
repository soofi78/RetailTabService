package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator


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
    @SerializedName("uom", alternate = arrayOf("unitName")) var uom: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("driverType") var driverType: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("changeunitflag") var changeunitflag: Boolean? = null

) : HistoryItemInterface {
    override fun getTitle(): String {
        return (productName ?: "")
    }

    override fun getDescription(): String {
        return uom ?: ""
    }

    override fun getAmount(): String {
        return "Qty: " + qty.toString().formatDecimalSeparator()
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }

    fun updateTotal() {
        totalPrice = qty * price
    }
}