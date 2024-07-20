package com.lfsolutions.retail.model.service

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.memo.ProductBatchList


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
    @SerializedName("price") var price: Int? = null,
    @SerializedName("unitPrice") var unitPrice: Int? = null,
    @SerializedName("averageCost") var averageCost: Int? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int? = null,
    @SerializedName("lastSellingPrice") var lastSellingPrice: Int? = null,
    @SerializedName("lastPurchasePrice") var lastPurchasePrice: Int? = null,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("changeunitflag") var changeunitflag: Boolean? = null,
    @SerializedName("actionType") var actionType: String? = null,
    @SerializedName("transType") var transType: String? = null,
    @SerializedName("transTypeDisplayText") @Transient var transTypeDisplayText: String? = null,
) {
    fun getSerialNumbers(): CharSequence? {
        var serials = ""
        productBatchList?.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials +=
                if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }
}