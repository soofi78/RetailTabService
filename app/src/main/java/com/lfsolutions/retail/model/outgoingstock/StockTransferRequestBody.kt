package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName


data class StockTransferRequestBody(
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("ToLocationId") var ToLocationId: Int? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @Transient var customerId: Int? = null,
    @SerializedName("stockTransferDetails") var stockTransferDetails: ArrayList<StockTransferProduct> = arrayListOf()
) {
    fun addEquipment(product: StockTransferProduct) {
        product.customerId = customerId
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