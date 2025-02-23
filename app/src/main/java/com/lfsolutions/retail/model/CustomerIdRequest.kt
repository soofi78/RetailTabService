package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class CustomerIdRequest(@SerializedName("customerId") var id: Int? = 0)

data class CustomersAndSalesId(
    @SerializedName("customerids") var customerids: ArrayList<Int>? = null,
    @SerializedName("salesOrderIds") var salesOrderIds: ArrayList<Int>? = null
)

data class CustomerAndSaleId(
    @SerializedName("customerId") var customerId: Int = 0,
    @SerializedName("salesOrderId") var salesOrderId: Int? = null
)