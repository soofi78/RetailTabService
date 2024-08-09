package com.lfsolutions.retail.model.sale.order.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.sale.TaxForProduct
import com.lfsolutions.retail.ui.documents.history.SaleOrderInvoiceItem
import com.lfsolutions.retail.util.formatDecimalSeparator


data class SalesOrderDetailRes(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("salesOrderId") var salesOrderId: Int? = null,
    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("qty") var qty: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("averageCost") var averageCost: Int? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("itemDiscountPerc") var itemDiscountPerc: Int? = null,
    @SerializedName("itemDiscount") var itemDiscount: Int? = null,
    @SerializedName("subTotal") var subTotal: Int? = null,
    @SerializedName("tax") var tax: Int? = null,
    @SerializedName("netTotal") var netTotal: Int? = null,
    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("taxForProduct") var taxForProduct: ArrayList<TaxForProduct> = arrayListOf(),
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("productBarcode") var productBarcode: String? = null,
    @SerializedName("totalValue") var totalValue: Int? = null,
    @SerializedName("qtyStock") var qtyStock: Int? = null,
    @SerializedName("lastPurchasePrice") var lastPurchasePrice: Int? = null,
    @SerializedName("netDiscount") var netDiscount: Int? = null,
    @SerializedName("isBatch") var isBatch: Boolean? = null,
    @SerializedName("vendorId") var vendorId: String? = null,
    @SerializedName("poQuantity") var poQuantity: Int? = null,
    @SerializedName("orderedQuantity") var orderedQuantity: Int? = null,
    @SerializedName("departmentId") var departmentId: Int? = null,
    @SerializedName("categoryId") var categoryId: Int? = null,
    @SerializedName("brandId") var brandId: String? = null,
    @SerializedName("netCost") var netCost: Int? = null,
    @SerializedName("priceGroupId") var priceGroupId: String? = null,
    @SerializedName("transactionRemarks") var transactionRemarks: String? = null,
    @SerializedName("purchaseOrderId") var purchaseOrderId: String? = null,
    @SerializedName("salesPersonId") var salesPersonId: String? = null,
    @SerializedName("soNo") var soNo: String? = null,
    @SerializedName("productGroup") var productGroup: String? = null,
    @SerializedName("parentId") var parentId: Int? = null,
    @SerializedName("productAttributes") var productAttributes: String? = null,
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