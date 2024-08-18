package com.lfsolutions.retail.model.sale.invoice

import android.util.Log
import com.lfsolutions.retail.model.memo.ProductBatchList

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.ApplicableTaxes


data class SalesInvoiceDetail(

    @SerializedName("Id") var Id: String? = null,
    @SerializedName("SalesInvoiceId") var SalesInvoiceId: Int? = 0,
    @SerializedName("ProductId") var ProductId: Int? = null,
    @SerializedName("InventoryCode") var InventoryCode: String? = null,
    @SerializedName("ProductName") var ProductName: String? = null,
    @SerializedName("ProductImage") @Transient var ProductImage: String? = null,
    @SerializedName("Qty") var Qty: Double = 0.0,
    @SerializedName("Price") var Price: Double? = null,
    @SerializedName("UnitId") var UnitId: Int? = null,
    @SerializedName("UnitName") var UnitName: String? = null,
    @SerializedName("ItemDiscountPerc") var ItemDiscountPerc: Double? = 0.0,
    @SerializedName("ItemDiscount") var ItemDiscount: Double? = null,
    @SerializedName("SubTotal") var SubTotal: Double = 0.0,
    @SerializedName("Tax") var Tax: Double = 0.0,
    @SerializedName("TaxRate") @Transient var TaxRate: Double = 0.0,
    @SerializedName("NetTotal") var NetTotal: Double = 0.0,
    @SerializedName("HSNCode") var HSNCode: String? = null,
    @SerializedName("SlNo") var SlNo: Int? = null,
    @SerializedName("Remarks") var Remarks: String? = null,
    @SerializedName("TaxForProduct") var TaxForProduct: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("ApplicableTaxes") var ApplicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("ProductBarcode") var ProductBarcode: String? = null,
    @SerializedName("AverageCost") var AverageCost: Int? = 0,
    @SerializedName("NetCost") var NetCost: Double? = 0.0,
    @SerializedName("CostWithoutTax") @Transient var CostWithoutTax: Double = 0.0,
    @SerializedName("DepartmentId") var DepartmentId: Int? = null,
    @SerializedName("SalesAccountId") var SalesAccountId: String? = null,
    @SerializedName("QtyStock") var QtyStock: Double? = 0.0,
    @SerializedName("LastPurchasePrice") var LastPurchasePrice: Double? = null,
    @SerializedName("SellingPrice") var SellingPrice: Double? = 0.0,
    @SerializedName("MRP") var MRP: Int? = null,
    @SerializedName("Locations") var Locations: String? = null,
    @SerializedName("Vendors") var Vendors: String? = null,
    @SerializedName("Departments") var Departments: String? = null,
    @SerializedName("Categories") var Categories: String? = null,
    @SerializedName("Brands") var Brands: String? = null,
    @SerializedName("Publishers") var Publishers: String? = null,
    @SerializedName("Authors") var Authors: String? = null,
    @SerializedName("Products") var Products: String? = null,
    @SerializedName("LocationGroup") var LocationGroup: String? = null,
    @SerializedName("Payments") var Payments: String? = null,
    @SerializedName("Type") var Type: String? = null,
    @SerializedName("ProductBatchList") var ProductBatchList: ArrayList<ProductBatchList> = arrayListOf(),
    @SerializedName("ItemSerialNumber") var ItemSerialNumber: String? = null,
    @SerializedName("TotalValue") var TotalValue: Double = 0.0,
    @SerializedName("NetDiscount") var NetDiscount: Double = 0.0,
    @SerializedName("IsExchange") var IsExchange: Boolean? = null,
    @SerializedName("IsExpire") var IsExpire: Boolean? = null,
    @SerializedName("IsBatch") var IsBatch: Boolean? = null,
    @SerializedName("IsFOC") var IsFOC: Boolean? = null,
    @SerializedName("PriceGroupId") var PriceGroupId: String? = null,
    @SerializedName("TransactionRemarks") var TransactionRemarks: String? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean? = false,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: Int? = null

) {
    fun getSerialNumbers(): String {
        var serials = ""
        ProductBatchList?.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials +=
                if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }

    override fun equals(other: Any?): Boolean {
        if (ProductId == (other as SalesInvoiceDetail).ProductId)
            return true
        return super.equals(other)
    }
}