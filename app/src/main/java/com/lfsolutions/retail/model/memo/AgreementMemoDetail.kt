package com.lfsolutions.retail.model.memo

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime


data class AgreementMemoDetail(
    @SerializedName("Id", alternate = arrayOf("id")) var Id: String? = null,
    @SerializedName("AgreementMemoId", alternate = arrayOf("agreementMemoId")) var AgreementMemoId: Int? = 0,
    @SerializedName("ProductId", alternate = arrayOf("productId")) var ProductId: Int? = null,
    @SerializedName(
        "InventoryCode",
        alternate = arrayOf("inventoryCode")
    ) var InventoryCode: String? = null,
    @SerializedName("Barcode", alternate = arrayOf("barcode")) var Barcode: String? = null,
    @SerializedName(
        "ProductName",
        alternate = arrayOf("productName")
    ) var ProductName: String? = null,
    @SerializedName("Qty", alternate = arrayOf("qty")) var Qty: Double? = null,
    @SerializedName("Cost", alternate = arrayOf("cost")) var Cost: Double? = null,
    @SerializedName("UnitId", alternate = arrayOf("unitId")) var UnitId: Int? = null,
    @SerializedName("UnitName", alternate = arrayOf("unitName")) var UnitName: String? = null,
    @SerializedName("TotalCost", alternate = arrayOf("totalCost")) var TotalCost: Double? = null,
    @SerializedName("SlNo", alternate = arrayOf("slNo")) var SlNo: Int? = null,
    @SerializedName("Remarks", alternate = arrayOf("remarks")) var Remarks: String? = null,
    @SerializedName("Type", alternate = arrayOf("type")) var Type: String? = null,
    @SerializedName(
        "AgreementType",
        alternate = arrayOf("agreementType")
    ) var AgreementType: String? = null,
    @SerializedName(
        "AgreementTypeString",
        alternate = arrayOf("agreementTypeString")
    ) var AgreementTypeDisplayText: String? = null,
    @SerializedName(
        "ItemSerialNumber",
        alternate = arrayOf("itemSerialNumber")
    ) var ItemSerialNumber: String? = null,
    @SerializedName(
        "ProductBatchList",
        alternate = arrayOf("productBatchList")
    ) var ProductBatchList: ArrayList<ProductBatchList>? = arrayListOf(),
    @SerializedName("Price", alternate = arrayOf("price")) var Price: Double? = 0.0,
    @SerializedName("TotalPrice", alternate = arrayOf("totalPrice")) var TotalPrice: Double? = 0.0,
    @SerializedName(
        "ProductGroup",
        alternate = arrayOf("productGroup")
    ) var ProductGroup: String? = null,
    @SerializedName("IsBatch", alternate = arrayOf("isBatch")) var IsBatch: Boolean? = null,
    @SerializedName("QtyOnHand", alternate = arrayOf("qtyOnHand")) var QtyOnHand: Double? = null,
    @SerializedName("IsAsset", alternate = arrayOf("isAsset")) var IsAsset: Boolean? = null,
    @SerializedName("IsDeleted", alternate = arrayOf("isDeleted")) var IsDeleted: Boolean? = null,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: String? = null,
    @SerializedName("changeunitflag") var changeunitflag: Boolean? = null
) : HistoryItemInterface {
    override fun getTitle(): String {
        return ProductName.toString()
    }

    override fun getDescription(): String {
        return UnitName.toString() + "/" + Qty
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + TotalCost
    }


    fun getSerialNumbers(): String {
        var serials = ""
        ProductBatchList?.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials +=
                if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }

    override fun getSerializedNumber(): String {
        return SlNo.toString()
    }

    override fun getFormattedCreationTime():String{
        val date = DateTime.getDateFromString(
            CreationTime?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateTimetRetailFormat)
        return formatted ?: CreationTime ?: ""
    }
}