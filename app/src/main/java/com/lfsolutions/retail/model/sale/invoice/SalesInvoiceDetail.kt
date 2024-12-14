package com.lfsolutions.retail.model.sale.invoice

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.sale.TaxForProduct
import com.lfsolutions.retail.model.sale.order.SalesOrderDetail
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator


data class SalesInvoiceDetail(

    @SerializedName("Id", alternate = arrayOf("id")) var id: Int? = null,
    @SerializedName(
        "SalesInvoiceId",
        alternate = arrayOf("salesInvoiceId")
    ) var salesInvoiceId: Int? = null,
    @SerializedName("ProductId", alternate = arrayOf("productId")) var productId: Int? = null,
    @SerializedName(
        "InventoryCode",
        alternate = arrayOf("inventoryCode")
    ) var inventoryCode: String? = null,
    @SerializedName(
        "ProductName",
        alternate = arrayOf("productName")
    ) var productName: String? = null,
    @SerializedName("productImage") var productImage: String? = null,
    @SerializedName("Qty", alternate = arrayOf("qty")) var qty: Double? = 0.0,
    @SerializedName("Price", alternate = arrayOf("price")) var price: Double? = 0.0,
    @SerializedName("UnitId", alternate = arrayOf("unitId")) var unitId: Int? = null,
    @SerializedName("UnitName", alternate = arrayOf("unitName")) var unitName: String? = null,
    @SerializedName(
        "ItemDiscountPerc",
        alternate = arrayOf("itemDiscountPerc")
    ) var itemDiscountPerc: Double? = null,
    @SerializedName(
        "ItemDiscount",
        alternate = arrayOf("itemDiscount")
    ) var itemDiscount: Double? = null,
    @SerializedName("SubTotal", alternate = arrayOf("subTotal")) var subTotal: Double? = null,
    @SerializedName("Tax", alternate = arrayOf("tax")) var tax: Double? = null,
    @SerializedName("TaxRate", alternate = arrayOf("taxRate")) var taxRate: Double = 0.0,
    @SerializedName(
        "CostWithoutTax",
        alternate = arrayOf("costWithoutTax")
    ) var costWithoutTax: Double = 0.0,
    @SerializedName("NetTotal", alternate = arrayOf("netTotal")) var netTotal: Double? = null,
    @SerializedName("HsnCode", alternate = arrayOf("hsnCode")) var hsnCode: String? = null,
    @SerializedName("SlNo", alternate = arrayOf("slNo")) var slNo: Int? = null,
    @SerializedName("Remarks", alternate = arrayOf("remarks")) var remarks: String? = null,
    @SerializedName(
        "TaxForProduct",
        alternate = arrayOf("taxForProduct")
    ) var taxForProduct: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName(
        "ApplicableTaxes",
        alternate = arrayOf("applicableTaxes")
    ) var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName(
        "ProductBarcode",
        alternate = arrayOf("productBarcode")
    ) var productBarcode: String? = null,
    @SerializedName(
        "AverageCost",
        alternate = arrayOf("averageCost")
    ) var averageCost: Double? = null,
    @SerializedName("NetCost", alternate = arrayOf("netCost")) var netCost: Double? = null,
    @SerializedName(
        "DepartmentId",
        alternate = arrayOf("departmentId")
    ) var departmentId: Int? = null,
    @SerializedName(
        "SalesAccountId",
        alternate = arrayOf("salesAccountId")
    ) var salesAccountId: String? = null,
    @SerializedName("QtyStock", alternate = arrayOf("qtyStock")) var qtyStock: Double? = null,
    @SerializedName(
        "LastPurchasePrice",
        alternate = arrayOf("lastPurchasePrice")
    ) var lastPurchasePrice: Double? = null,
    @SerializedName(
        "SellingPrice",
        alternate = arrayOf("sellingPrice")
    ) var sellingPrice: Double? = null,
    @SerializedName("MRP", alternate = arrayOf("mrp")) var mrp: Int? = null,
    @SerializedName("Locations", alternate = arrayOf("locations")) var locations: String? = null,
    @SerializedName("Vendors", alternate = arrayOf("vendors")) var vendors: String? = null,
    @SerializedName(
        "Departments",
        alternate = arrayOf("departments")
    ) var departments: String? = null,
    @SerializedName("Categories", alternate = arrayOf("categories")) var categories: String? = null,
    @SerializedName("Brands", alternate = arrayOf("brands")) var brands: String? = null,
    @SerializedName("Publishers", alternate = arrayOf("publishers")) var publishers: String? = null,
    @SerializedName("Authors", alternate = arrayOf("authors")) var authors: String? = null,
    @SerializedName("Products", alternate = arrayOf("products")) var products: String? = null,
    @SerializedName(
        "LocationGroup",
        alternate = arrayOf("locationGroup")
    ) var locationGroup: String? = null,
    @SerializedName("Payments", alternate = arrayOf("payments")) var payments: String? = null,
    @SerializedName("Type", alternate = arrayOf("type")) var type: String? = null,
    @SerializedName(
        "ProductBatchList",
        alternate = arrayOf("productBatchList")
    ) var productBatchList: ArrayList<ProductBatchList>? = null,
    @SerializedName(
        "ItemSerialNumber",
        alternate = arrayOf("itemSerialNumber")
    ) var itemSerialNumber: String? = null,
    @SerializedName("TotalValue", alternate = arrayOf("totalValue")) var totalValue: Double? = null,
    @SerializedName(
        "NetDiscount",
        alternate = arrayOf("netDiscount")
    ) var netDiscount: Double? = 0.0,
    @SerializedName(
        "IsExchange",
        alternate = arrayOf("isExchange")
    ) var isExchange: Boolean? = false,

    @SerializedName(
        "changeunitflag"
    ) var changeunitflag: Boolean? = false,
    @SerializedName("IsExpire", alternate = arrayOf("isExpire")) var isExpire: Boolean? = false,
    @SerializedName("IsBatch", alternate = arrayOf("isBatch")) var isBatch: Boolean? = false,
    @SerializedName("IsFOC", alternate = arrayOf("isFOC")) var isFOC: Boolean? = false,
    @SerializedName(
        "PriceGroupId",
        alternate = arrayOf("priceGroupId")
    ) var priceGroupId: String? = null,
    @SerializedName(
        "TransactionRemarks",
        alternate = arrayOf("transactionRemarks")
    ) var transactionRemarks: String? = null,
    @SerializedName("IsDeleted", alternate = arrayOf("isDeleted")) var isDeleted: Boolean? = null,
    @SerializedName(
        "DeleterUserId",
        alternate = arrayOf("deleterUserId")
    ) var deleterUserId: String? = null,
    @SerializedName(
        "DeletionTime",
        alternate = arrayOf("deletionTime")
    ) var deletionTime: String? = null,
    @SerializedName(
        "LastModificationTime",
        alternate = arrayOf("lastModificationTime")
    ) var lastModificationTime: String? = null,
    @SerializedName(
        "LastModifierUserId",
        alternate = arrayOf("lastModifierUserId")
    ) var lastModifierUserId: String? = null,
    @SerializedName(
        "CreationTime",
        alternate = arrayOf("creationTime")
    ) var creationTime: String? = null,
    @SerializedName(
        "CreatorUserId",
        alternate = arrayOf("creatorUserId")
    ) var creatorUserId: String? = null,
) : HistoryItemInterface {
    override fun getTitle(): String {
        return productName.toString()
    }

    override fun getDescription(): String {
        return "$unitName x$qty"
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + netTotal?.formatDecimalSeparator()
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }

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
        if (productId == (other as SalesInvoiceDetail).productId)
            return true
        return super.equals(other)
    }

}