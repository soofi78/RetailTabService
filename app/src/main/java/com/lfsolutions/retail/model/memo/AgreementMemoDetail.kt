package com.lfsolutions.retail.model.memo

import android.util.Log
import com.google.gson.annotations.SerializedName


data class AgreementMemoDetail(
    @SerializedName("Id") var Id: String? = null,
    @SerializedName("AgreementMemoId") var AgreementMemoId: Int? = 0,
    @SerializedName("ProductId") var ProductId: Int? = null,
    @SerializedName("InventoryCode") var InventoryCode: String? = null,
    @SerializedName("Barcode") var Barcode: String? = null,
    @SerializedName("ProductName") var ProductName: String? = null,
    @SerializedName("Qty") var Qty: Double? = null,
    @SerializedName("Cost") var Cost: Double? = null,
    @SerializedName("UnitId") var UnitId: Int? = null,
    @SerializedName("UnitName") var UnitName: String? = null,
    @SerializedName("TotalCost") var TotalCost: Double? = null,
    @SerializedName("SlNo") var SlNo: Int? = null,
    @SerializedName("Remarks") var Remarks: String? = null,
    @SerializedName("Type") var Type: String? = null,
    @SerializedName("AgreementType") var AgreementType: String? = null,
    @SerializedName("AgreementTypeDisplayText") var AgreementTypeDisplayText: String? = null,
    @SerializedName("ItemSerialNumber") var ItemSerialNumber: String? = null,
    @SerializedName("ProductBatchList") var ProductBatchList: ArrayList<ProductBatchList> = arrayListOf(),
    @SerializedName("Price") var Price: Double? = 0.0,
    @SerializedName("TotalPrice") var TotalPrice: Double? = 0.0,
    @SerializedName("ProductGroup") var ProductGroup: String? = null,
    @SerializedName("IsBatch") var IsBatch: Boolean? = null,
    @SerializedName("QtyOnHand") var QtyOnHand: Double? = null,
    @SerializedName("IsAsset") var IsAsset: Boolean? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean? = null,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: String? = null
) {
    fun getSerialNumbers(): String {
        var serials = ""
        ProductBatchList.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials +=
                if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }
}