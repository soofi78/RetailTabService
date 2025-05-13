package com.lfsolutions.retail.model

data class SaleOrderToStockReceive(
    val date: String,
    val locationId: Int?,
    val remarks: String,
    val salesOrderIds: List<Int>
)