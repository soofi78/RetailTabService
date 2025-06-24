package com.lfsolutions.retail.model.sale.order

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.sale.TaxForProduct
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail


data class SalesOrderDetail(

    @SerializedName("Id") var Id: String? = null,
    @SerializedName("SalesOrderId") var SalesOrderId: Int? = null,
    @SerializedName("ProductId") var ProductId: Int? = null,
    @SerializedName("InventoryCode") var InventoryCode: String? = null,
    @SerializedName("ProductName") var ProductName: String? = null,
    @SerializedName("ProductImage") @Transient var ProductImage: String? = null,
    @SerializedName("Qty") var Qty: Double = 0.0,
    @SerializedName("Price") var Price: Double = 0.0,
    @SerializedName("AverageCost") var AverageCost: Double = 0.0,
    @SerializedName("UnitId") var UnitId: Int? = null,
    @SerializedName("UnitName") var UnitName: String? = null,
    @SerializedName("ItemDiscountPerc") var ItemDiscountPerc: Double = 0.0,
    @SerializedName("ItemDiscount") var ItemDiscount: Double = 0.0,
    @SerializedName("SubTotal") var SubTotal: Double = 0.0,
    @SerializedName("Tax") var Tax: Double = 0.0,
    @SerializedName("TaxRate") @Transient var TaxRate: Double = 0.0,
    @SerializedName("NetTotal") var NetTotal: Double = 0.0,
    @SerializedName("SlNo") var SlNo: Int? = 0,
    @SerializedName("Remarks") var Remarks: String? = null,
    @SerializedName("TaxForProduct") var TaxForProduct: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("ApplicableTaxes") var ApplicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("ProductBarcode") var ProductBarcode: String? = null,
    @SerializedName("TotalValue") var TotalValue: Double = 0.0,
    @SerializedName("QtyStock") var QtyStock: Double? = 0.0,
    @SerializedName("LastPurchasePrice") var LastPurchasePrice: Double = 0.0,
    @SerializedName("NetDiscount") var NetDiscount: Double = 0.0,
    @SerializedName("IsBatch") var IsBatch: Boolean? = null,
    @SerializedName("VendorId") var VendorId: String? = null,
    @SerializedName("POQuantity") var POQuantity: Int? = 0,
    @SerializedName("OrderedQuantity") var OrderedQuantity: Int? = 0,
    @SerializedName("DepartmentId") var DepartmentId: Int? = null,
    @SerializedName("CategoryId") var CategoryId: Int? = null,
    @SerializedName("BrandId") var BrandId: String? = null,
    @SerializedName("NetCost") var NetCost: Double = 0.0,
    @SerializedName("CostWithoutTax") @Transient var CostWithoutTax: Double = 0.0,
    @SerializedName("PriceGroupId") var PriceGroupId: String? = null,
    @SerializedName("TransactionRemarks") var TransactionRemarks: String? = null,
    @SerializedName("PurchaseOrderId") var PurchaseOrderId: String? = null,
    @SerializedName("SalesPersonId") var SalesPersonId: String? = null,
    @SerializedName("SoNo") var SoNo: String? = null,
    @SerializedName("ProductGroup") var ProductGroup: String? = null,
    @SerializedName("ParentId") var ParentId: Int? = null,
    @SerializedName("ProductAttributes") var ProductAttributes: String? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean? = null,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: Int? = null,
    @SerializedName("ProductBatchList") var productBatchList: ArrayList<ProductBatchList>? = null,


    ) {
    fun getSerialNumbers(): String {
        var serials = ""
        productBatchList?.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials +=
                if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }

    override fun equals(other: Any?): Boolean {
        if (ProductId == (other as SalesOrderDetail).ProductId)
            return true
        return super.equals(other)
    }
}