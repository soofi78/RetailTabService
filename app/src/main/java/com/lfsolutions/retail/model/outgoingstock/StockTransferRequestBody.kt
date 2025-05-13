package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.CustomerAndSaleId


data class StockTransferRequestBody(
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("toLocationId") var ToLocationId: Int? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("customerSalesorderIds") var customerSalesorderIds: ArrayList<CustomerAndSaleId> = arrayListOf(),
//    @SerializedName("customerIds") var customerIds: ArrayList<Int>? = null,
//    @SerializedName("SalesOrderIds") var SalesOrderIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("stockTransferDetails") var stockTransferDetails: ArrayList<StockTransferProduct> = arrayListOf()
) {
    fun addEquipment(product: StockTransferProduct) {
        if (customerSalesorderIds.size > 0) {
            product.customerId = customerSalesorderIds.get(0).customerId
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