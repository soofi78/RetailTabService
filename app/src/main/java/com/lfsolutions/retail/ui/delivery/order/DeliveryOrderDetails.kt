package com.lfsolutions.retail.ui.delivery.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface

data class DeliveryOrderDetails(
    var changeunitflag: Boolean? = false,
    var cost: Double? = 0.0,
    var deliverQty: Double? = null,
    var deliveredQty: Double? = null,
    var focQty: Int? = 0,
    var id: Int? = null,
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
    @SerializedName("uom", alternate = arrayOf("unitName")) var uom: String? = "",
    var creationTime: String? = null,
    var creatorUserId: Int? = null
) : HistoryItemInterface {
    override fun getTitle(): String {
        return inventoryCode.toString() + " - " + productName.toString()
    }

    override fun getDescription(): String {
        return "$uom x$qty"
    }

    override fun getAmount(): String {
        return "Delivered: $deliveredQty"
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }

}