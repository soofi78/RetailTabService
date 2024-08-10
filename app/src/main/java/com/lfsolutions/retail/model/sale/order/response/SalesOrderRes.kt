package com.lfsolutions.retail.model.sale.order.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime


data class SalesOrderRes(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("soNo") var soNo: String? = null,
    @SerializedName("soDate") var soDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("erpInternalId") var erpInternalId: String? = null,
    @SerializedName("deliveryDate") var deliveryDate: String? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("memberId") var memberId: String? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("isCancelled") var isCancelled: Boolean? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("soItemDiscount") var soItemDiscount: Int? = null,
    @SerializedName("soNetDiscountPerc") var soNetDiscountPerc: Int? = null,
    @SerializedName("soNetDiscount") var soNetDiscount: Int? = null,
    @SerializedName("soSubTotal") var soSubTotal: Int? = null,
    @SerializedName("soTax") var soTax: Int? = null,
    @SerializedName("soNetTotal") var soNetTotal: Int? = null,
    @SerializedName("soRoundingAmount") var soRoundingAmount: Int? = null,
    @SerializedName("otherCharges") var otherCharges: Int? = null,
    @SerializedName("soGrandTotal") var soGrandTotal: Int? = null,
    @SerializedName("salespersonId") var salespersonId: Int? = null,
    @SerializedName("deliveryPersonId") var deliveryPersonId: String? = null,
    @SerializedName("employeeId") var employeeId: String? = null,
    @SerializedName("soTotalValue") var soTotalValue: Int? = null,
    @SerializedName("profitMargin") var profitMargin: Int? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("creditDays") var creditDays: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("salesOrderStatusId") var salesOrderStatusId: Int? = null,
    @SerializedName("paymentTermId") var paymentTermId: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("salesPersonName") var salesPersonName: String? = null,
    @SerializedName("branchId") var branchId: String? = null,
    @SerializedName("taxName") var taxName: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("paidAmount") var paidAmount: Int? = null,
    @SerializedName("balance") var balance: Int? = null,
    @SerializedName("usedAmount") var usedAmount: Int? = null,
    @SerializedName("fromLocationId") var fromLocationId: String? = null,
    @SerializedName("salesInvoiceId") var salesInvoiceId: String? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("fromLocationName") var fromLocationName: String? = null,
    @SerializedName("orderedQty") var orderedQty: Int? = null,
    @SerializedName("receivedQty") var receivedQty: Int? = null,
    @SerializedName("balanceQty") var balanceQty: Int? = null,
    @SerializedName("notified") var notified: Boolean? = null,
    @SerializedName("collected") var collected: Boolean? = null,
    @SerializedName("binLocation") var binLocation: String? = null,
    @SerializedName("isAutoPayment") var isAutoPayment: Boolean? = null,
    @SerializedName("paymentReference") var paymentReference: String? = null,
    @SerializedName("currencyId") var currencyId: String? = null,
    @SerializedName("currencyRate") var currencyRate: Int? = null,
    @SerializedName("isLuggageAccepted") var isLuggageAccepted: Boolean? = null,
    @SerializedName("module") var module: Int? = null,
    @SerializedName("poQuantity") var poQuantity: Int? = null,
    @SerializedName("orderedQuantity") var orderedQuantity: Int? = null,
    @SerializedName("addOn") var addOn: String? = null,
    @SerializedName("purchaseOrderId") var purchaseOrderId: String? = null,
    @SerializedName("walkInCustomerDetail") var walkInCustomerDetail: String? = null,
    @SerializedName("walkInCustomer") var walkInCustomer: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: Int? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null,
    @SerializedName("signature") var signature: String? = null,

    ) {
    fun InvoiceDateFormatted(): String {
        val date = DateTime.getDateFromString(
            soDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: soDate ?: ""
    }

    fun StatusFormatted(): String {
        return when (status) {
            "I" -> "INVOICED"
            "A" -> "APPROVED"
            "P" -> "PENDING"
            else -> status.toString()
        }
    }

    fun InvoiceNetTotalFromatted(): String {
        return Main.app.getSession().currencySymbol + soGrandTotal?.formatDecimalSeparator()
    }

    fun BalanceFormatted(): String {
        return Main.app.getSession().currencySymbol + balance?.formatDecimalSeparator()
    }

    fun signatureUrl(): String {
        return Main.app.getBaseUrl() + signature
    }
}