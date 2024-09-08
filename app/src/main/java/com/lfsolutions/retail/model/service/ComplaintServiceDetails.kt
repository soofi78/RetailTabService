package com.lfsolutions.retail.model.service

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime


data class ComplaintServiceDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("transactionRemarks") var transactionRemarks: String? = null,
    @SerializedName("qty") var qty: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("complaintTypes") var complaintTypes: ArrayList<ComplaintTypes> = arrayListOf(),
    @SerializedName("productBatchList") var productBatchList: ArrayList<ProductBatchList> = arrayListOf(),
    @SerializedName("type") var type: String? = null,
    @SerializedName("serviceTypes") var serviceTypes: ArrayList<String> = arrayListOf(),
    @SerializedName("price") var price: Double = 0.0,
    @SerializedName("unitPrice") var unitPrice: Double? = null,
    @SerializedName("averageCost") var averageCost: Double = 0.0,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int? = null,
    @SerializedName("lastSellingPrice") var lastSellingPrice: Double = 0.0,
    @SerializedName("lastPurchasePrice") var lastPurchasePrice: Double = 0.0,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("changeunitflag") var changeunitflag: Boolean = false,
    @SerializedName("actionType") var actionType: String? = null,
    @SerializedName("transType") var transType: String? = null,
    @SerializedName("transTypeDisplayText") @Transient var transTypeDisplayText: String? = null,

    ) : HistoryItemInterface {
    override fun getTitle(): String {
        return productName.toString()
    }

    override fun getDescription(): String {
        return unitName.toString()
    }

    override fun getAmount(): String {
        return qty.toString()
    }

    fun getSerialNumbers(): CharSequence? {
        var serials = ""
        productBatchList.forEach {
            serials +=
                if (serials == "") it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }
}