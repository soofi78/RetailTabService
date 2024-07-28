package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName


data class SaleOrderRequest (

  @SerializedName("salesOrder") var salesOrder       : SalesOrder?                 = SalesOrder(),
  @SerializedName("salesOrderDetail") var salesOrderDetail : ArrayList<SalesOrderDetail> = arrayListOf()

)
