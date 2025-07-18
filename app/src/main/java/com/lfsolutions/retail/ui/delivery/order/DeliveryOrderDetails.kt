package com.lfsolutions.retail.ui.delivery.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface

data class DeliveryOrderDetails(
    var changeunitflag: Boolean? = false,
    var cost: Double? = 0.0,
    var costWithoutTax: Double? = 0.0,
    var deliverQty: Double? = null,
    var deliveredQty: Double? = null,
    var focQty: Int? = 0,
    var id: Int? = null,
    var inventoryCode: String? = null,
    var isNewItem: Boolean? = true,
    //var productBatchList: List<ProductBatchList> = arrayListOf(),
    var productId: Int? = null,
    var productName: String? = null,
    var productImage: String? = null,
    var qty: Double? = 0.0,
    var slNo: Int? = null,
    var transactionRemarks: String? = null,
    var unitId: Int? = null,
    @SerializedName("uom", alternate = arrayOf("unitName")) var uom: String? = "",
    var creationTime: String? = null,
    var creatorUserId: Int? = null,
    @SerializedName("ProductBatchList", alternate = arrayOf("productBatchList"))
    var productBatchList: ArrayList<ProductBatchList>? = null,
    @SerializedName("Type", alternate = arrayOf("type")) var type: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = false,
    var isSerialNumberAdeded: Boolean = false
) : HistoryItemInterface {

    fun isAddSerialButtonVisible():Boolean{
        return type=="S" && isAsset==true && !isSerialNumberAdeded
    }
    override fun getTitle(): String {
        return inventoryCode.toString() + " - " + productName.toString()
    }

    override fun getDescription(): String {
        return "$uom x$deliveredQty"
    }

    override fun getAmount(): String {
        return "Delivered: $deliveredQty"
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }
    fun getPreSelectedSerialNumbers(serialNumbersList: ArrayList<SerialNumber>): ArrayList<out MultiSelectModelInterface> {
        val serials = ArrayList<MultiSelectModelInterface>()
        productBatchList?.forEach {
            val index = serialNumbersList.indexOf(SerialNumber(id = it.Id))
            if (index > -1) {
                serials.add(serialNumbersList[index])
            }
        }
        return serials
    }
}