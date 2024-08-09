package com.lfsolutions.retail.model.sale.invoice.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.sale.TaxForProduct
import com.lfsolutions.retail.ui.documents.history.SaleOrderInvoiceItem
import com.lfsolutions.retail.util.formatDecimalSeparator


data class SalesInvoiceDetailRes(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("salesInvoiceId") var salesInvoiceId: Int? = null,
    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("qty") var qty: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("itemDiscountPerc") var itemDiscountPerc: Int? = null,
    @SerializedName("itemDiscount") var itemDiscount: Int? = null,
    @SerializedName("subTotal") var subTotal: Int? = null,
    @SerializedName("tax") var tax: Int? = null,
    @SerializedName("netTotal") var netTotal: Int? = null,
    @SerializedName("hsnCode") var hsnCode: String? = null,
    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("taxForProduct") var taxForProduct: ArrayList<TaxForProduct> = arrayListOf(),
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("productBarcode") var productBarcode: String? = null,
    @SerializedName("averageCost") var averageCost: Int? = null,
    @SerializedName("netCost") var netCost: Int? = null,
    @SerializedName("departmentId") var departmentId: Int? = null,
    @SerializedName("salesAccountId") var salesAccountId: String? = null,
    @SerializedName("qtyStock") var qtyStock: Int? = null,
    @SerializedName("lastPurchasePrice") var lastPurchasePrice: Int? = null,
    @SerializedName("sellingPrice") var sellingPrice: Int? = null,
    @SerializedName("mrp") var mrp: Int? = null,
    @SerializedName("locations") var locations: String? = null,
    @SerializedName("vendors") var vendors: String? = null,
    @SerializedName("departments") var departments: String? = null,
    @SerializedName("categories") var categories: String? = null,
    @SerializedName("brands") var brands: String? = null,
    @SerializedName("publishers") var publishers: String? = null,
    @SerializedName("authors") var authors: String? = null,
    @SerializedName("products") var products: String? = null,
    @SerializedName("locationGroup") var locationGroup: String? = null,
    @SerializedName("payments") var payments: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("productBatchList") var productBatchList: String? = null,
    @SerializedName("itemSerialNumber") var itemSerialNumber: String? = null,
    @SerializedName("totalValue") var totalValue: Int? = null,
    @SerializedName("netDiscount") var netDiscount: Int? = null,
    @SerializedName("isExchange") var isExchange: Boolean? = null,
    @SerializedName("isExpire") var isExpire: Boolean? = null,
    @SerializedName("isBatch") var isBatch: Boolean? = null,
    @SerializedName("isFOC") var isFOC: Boolean? = null,
    @SerializedName("priceGroupId") var priceGroupId: String? = null,
    @SerializedName("transactionRemarks") var transactionRemarks: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null

) : SaleOrderInvoiceItem {
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

}