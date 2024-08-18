package com.lfsolutions.retail.model.outgoingstock

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface


data class StockTransferProduct(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Long? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Long = 0,
    @SerializedName("cost") var cost: Double = 0.0,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("price") var price: Double = 0.0,
    @SerializedName("subTotal") var subTotal: Double = 0.0,
    @SerializedName("qty") var qty: Double = 0.0,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("productBatchList") var productBatchList: ArrayList<ProductBatchList> = arrayListOf(),
    @SerializedName("unit") var unit: String? = null,
    @SerializedName("totalPrice") var totalPrice: Double? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("stockTransferId") var stockTransferId: Int? = null,
    @SerializedName("productBarcode") var productBarcode: String? = null,
    @SerializedName("barCode") var barCode: String? = null,
    @SerializedName("totalCost") var totalCost: Double? = null,
    @SerializedName("receivedQty") var receivedQty: Double? = null,
    @SerializedName("requestedQty") var requestedQty: Double? = null,
    @SerializedName("diffQty") var diffQty: Double? = null,
    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("itemSerialNumber") var itemSerialNumber: String? = null,
    @SerializedName("productGroup") var productGroup: String? = null,
    @SerializedName("isStockReceiveReturn") var isStockReceiveReturn: Boolean? = null,
    @SerializedName("isBatch") var isBatch: Boolean? = null,
    @SerializedName("productAttributes") var productAttributes: String? = null,
    @SerializedName("departmentId") var departmentId: Int? = null,
    @SerializedName("purchaseAccountId") var purchaseAccountId: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null
) : HistoryItemInterface {

    fun updateTotal() {
        val taxAmount = (qty * price) * (getApplicableTaxRate().toDouble() / 100.0)
        val total = ((qty * price) + taxAmount)
        subTotal = total
    }

    fun getApplicableTaxRate(): Int {
        if (applicableTaxes == null || applicableTaxes?.isEmpty() == true)
            return 0

        var tax = 0
        applicableTaxes?.forEach {
            tax += it.taxRate ?: 0
        }
        return tax
    }

    fun getSerialNumbers(): String {
        var serials = ""
        productBatchList.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials += if (serials.equals("")) it.SerialNumber.toString() else "\n" + it.SerialNumber
        }
        return serials
    }

    fun getPreSelectedSerialNumbers(serialNumbersList: ArrayList<SerialNumber>): ArrayList<out MultiSelectModelInterface>? {
        val serials = ArrayList<MultiSelectModelInterface>()
        productBatchList.forEach {
            val index = serialNumbersList.indexOf(SerialNumber(id = it.Id))
            if (index > -1) {
                serials.add(serialNumbersList[index])
            }
        }
        return serials
    }

    override fun getTitle(): String {
        return productName.toString()
    }

    override fun getDescription(): String {
        return "$unitName x$qty"
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + totalPrice?.formatDecimalSeparator()
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }
}

