package com.lfsolutions.retail.model.sale.order.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.sale.order.Vendor


data class SaleOrderResponse(
    @SerializedName("salesOrder") var salesOrder: SalesOrderRes? = SalesOrderRes(),
    @SerializedName("salesOrderDetail") var salesOrderDetail: ArrayList<SalesOrderDetailRes> = arrayListOf(),
    @SerializedName("vendors") var vendors: ArrayList<Vendor> = arrayListOf()
)