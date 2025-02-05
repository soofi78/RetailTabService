package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class StockTransferRequestBody(
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("ToLocationId") var ToLocationId: Int? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("customerIds") var customerIds: ArrayList<Int>? = null,
    @SerializedName("SalesOrderIds") var SalesOrderIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("stockTransferDetails") var stockTransferDetails: ArrayList<StockTransferProduct> = arrayListOf()
) {
    fun addEquipment(product: StockTransferProduct) {
        if ((customerIds?.size ?: 0) > 0) {
            product.customerId = customerIds?.get(0)
        }
        stockTransferDetails.add(product)
    }

    fun calculateAndSerialize() {
        var count = 1
        stockTransferDetails.forEach {
            it.calculateAmount()
            it.slNo = count
            count++
        }
    }
}