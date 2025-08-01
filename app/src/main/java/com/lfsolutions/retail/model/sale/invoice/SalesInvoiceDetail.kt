package com.lfsolutions.retail.model.sale.invoice

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import java.util.UUID


data class SalesInvoiceDetail(
    @SerializedName("localId") var localId: String = UUID.randomUUID().toString(),
    @SerializedName("Id", alternate = arrayOf("id")) var id: Int? = null,
    @SerializedName(
        "SalesInvoiceId", alternate = arrayOf("salesInvoiceId")
    ) var salesInvoiceId: Int? = null,
    @SerializedName("ProductId", alternate = arrayOf("productId")) var productId: Int? = null,
    @SerializedName(
        "InventoryCode", alternate = arrayOf("inventoryCode")
    ) var inventoryCode: String? = null,
    @SerializedName(
        "ProductName", alternate = arrayOf("productName")
    ) var productName: String? = null,
    @SerializedName("productImage") var productImage: String? = null,
    @SerializedName("Qty", alternate = arrayOf("qty")) var qty: Double? = 0.0,
    @SerializedName("Price", alternate = arrayOf("price")) var price: Double? = 0.0,
    @SerializedName("UnitId", alternate = arrayOf("unitId")) var unitId: Int? = null,
    @SerializedName("UnitName", alternate = arrayOf("unitName")) var unitName: String? = null,
    @SerializedName(
        "ItemDiscountPerc", alternate = arrayOf("itemDiscountPerc")
    ) var itemDiscountPerc: Double? = 0.0,
    @SerializedName(
        "ItemDiscount", alternate = arrayOf("itemDiscount")
    ) var itemDiscount: Double? = 0.0,
    @SerializedName("SubTotal", alternate = arrayOf("subTotal")) var subTotal: Double? = 0.0,
    @SerializedName("Tax", alternate = arrayOf("tax")) var tax: Double? = 0.0,
    @SerializedName("TaxRate", alternate = arrayOf("taxRate")) var taxRate: Double = 0.0,
    @SerializedName("cost") var costWithoutTax: Double = 0.0,
    @SerializedName("NetTotal", alternate = arrayOf("netTotal")) var netTotal: Double? = 0.0,
    @SerializedName("HsnCode", alternate = arrayOf("hsnCode")) var hsnCode: String? = null,
    @SerializedName("SlNo", alternate = arrayOf("slNo")) var slNo: Int? = null,
    @SerializedName("Remarks", alternate = arrayOf("remarks")) var remarks: String? = null,
    @SerializedName(
        "TaxForProduct", alternate = arrayOf("taxForProduct")
    ) var taxForProduct: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName(
        "ApplicableTaxes", alternate = arrayOf("applicableTaxes")
    ) var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName(
        "ProductBarcode", alternate = arrayOf("productBarcode")
    ) var productBarcode: String? = null,
    @SerializedName(
        "AverageCost", alternate = arrayOf("averageCost")
    ) var averageCost: Double? = 0.0,
    @SerializedName("NetCost", alternate = arrayOf("netCost")) var netCost: Double? = 0.0,
    @SerializedName(
        "DepartmentI", alternate = arrayOf("departmentId")
    ) var departmentId: Int? = null,
    @SerializedName(
        "SalesAccountId", alternate = arrayOf("salesAccountId")
    ) var salesAccountId: String? = null,
    @SerializedName("QtyStock", alternate = arrayOf("qtyStock")) var qtyStock: Double? = 0.0,
    @SerializedName(
        "LastPurchasePrice", alternate = arrayOf("lastPurchasePrice")
    ) var lastPurchasePrice: Double? = 0.0,
    @SerializedName(
        "SellingPrice", alternate = arrayOf("sellingPrice")
    ) var sellingPrice: Double? = 0.0,
    @SerializedName("MRP", alternate = arrayOf("mrp")) var mrp: Int? = null,
    @SerializedName("Locations", alternate = arrayOf("locations")) var locations: String? = null,
    @SerializedName("Vendors", alternate = arrayOf("vendors")) var vendors: String? = null,
    @SerializedName(
        "Departments", alternate = arrayOf("departments")
    ) var departments: String? = null,
    @SerializedName("Categories", alternate = arrayOf("categories")) var categories: String? = null,
    @SerializedName("Brands", alternate = arrayOf("brands")) var brands: String? = null,
    @SerializedName("Publishers", alternate = arrayOf("publishers")) var publishers: String? = null,
    @SerializedName("Authors", alternate = arrayOf("authors")) var authors: String? = null,
    @SerializedName("Products", alternate = arrayOf("products")) var products: String? = null,
    @SerializedName(
        "LocationGroup", alternate = arrayOf("locationGroup")
    ) var locationGroup: String? = null,
    @SerializedName("Payments", alternate = arrayOf("payments")) var payments: String? = null,
    @SerializedName("Type", alternate = arrayOf("type")) var type: String? = null,
    @SerializedName(
        "ProductBatchList", alternate = arrayOf("productBatchList")
    ) var productBatchList: ArrayList<ProductBatchList>? = null,
    @SerializedName(
        "ItemSerialNumber", alternate = arrayOf("itemSerialNumber")
    ) var itemSerialNumber: String? = null,
    @SerializedName("TotalValue", alternate = arrayOf("totalValue")) var totalValue: Double? = 0.0,
    @SerializedName(
        "NetDiscount", alternate = arrayOf("netDiscount")
    ) var netDiscount: Double? = 0.0,
    @SerializedName(
        "IsExchange", alternate = arrayOf("isExchange")
    ) var isExchange: Boolean? = false,

    @SerializedName(
        "changeunitflag"
    ) var changeunitflag: Boolean? = false,
    @SerializedName("IsExpire", alternate = arrayOf("isExpire")) var isExpire: Boolean? = false,
    @SerializedName("IsBatch", alternate = arrayOf("isBatch")) var isBatch: Boolean? = false,
    @SerializedName("IsFOC", alternate = arrayOf("isFOC")) var isFOC: Boolean? = false,
    @SerializedName(
        "PriceGroupId", alternate = arrayOf("priceGroupId")
    ) var priceGroupId: String? = null,
    @SerializedName(
        "TransactionRemarks", alternate = arrayOf("transactionRemarks")
    ) var transactionRemarks: String? = null,
    @SerializedName("IsDeleted", alternate = arrayOf("isDeleted")) var isDeleted: Boolean? = null,
    @SerializedName(
        "DeleterUserId", alternate = arrayOf("deleterUserId")
    ) var deleterUserId: String? = null,
    @SerializedName(
        "DeletionTime", alternate = arrayOf("deletionTime")
    ) var deletionTime: String? = null,
    @SerializedName(
        "LastModificationTime", alternate = arrayOf("lastModificationTime")
    ) var lastModificationTime: String? = null,
    @SerializedName(
        "LastModifierUserId", alternate = arrayOf("lastModifierUserId")
    ) var lastModifierUserId: String? = null,
    @SerializedName(
        "CreationTime", alternate = arrayOf("creationTime")
    ) var creationTime: String? = null,
    @SerializedName(
        "CreatorUserId", alternate = arrayOf("creatorUserId")
    ) var creatorUserId: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = false,
    var isSerialNumberAdeded: Boolean = false
) : HistoryItemInterface {

    fun isAddSerialButtonVisible():Boolean{
        return type=="S" && isAsset==true && !isSerialNumberAdeded
    }

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
            serials += if (serials.equals("")) it.SerialNumber.toString() else " / " + it.SerialNumber
        }
        return serials
    }

    fun getSerialNumberText():String{
        var serials = ""
        productBatchList?.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials=it.SerialNumber ?:""
        }
        return ""
    }

    override fun equals(other: Any?): Boolean {
        if (localId == (other as SalesInvoiceDetail).localId) return true
        return super.equals(other)
    }

    fun getApplicableTaxRate(): Int {
        if (applicableTaxes == null || applicableTaxes.isEmpty() == true)
            return 0

        var tax = 0
        applicableTaxes.forEach {
            tax += it.taxRate ?: 0
        }
        return tax
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