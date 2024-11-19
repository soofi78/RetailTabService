package com.lfsolutions.retail.ui.delivery.order

import com.lfsolutions.retail.model.memo.ProductBatchList

data class DeliveryOrderDetail(
    var changeunitflag: Boolean? = false,
    var cost: Double? = 0.0,
    var deliverQty: Double? = null,
    var focQty: Double? = 0.0,
    var id: Double? = null,
    var inventoryCode: String? = null,
    var isNewItem: Boolean? = true,
    var productBatchList: List<ProductBatchList> = arrayListOf(),
    var productId: Int? = null,
    var productName: String? = null,
    var productImage: String? = null,
    var qty: Double? = 0.0,
    var slNo: Int? = null,
    var transactionRemarks: String? = null,
    var unitId: Int? = null,
    var uom: String? = "",
    var creationTime: String? = null,
    var creatorUserId: Int? = null
)