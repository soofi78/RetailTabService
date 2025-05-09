package com.lfsolutions.retail.model.sale.order.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator


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
    @SerializedName("customerCode") var customerCode: String? = null,
    @SerializedName("isCancelled") var isCancelled: Boolean? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("soItemDiscount") var soItemDiscount: Double? = null,
    @SerializedName("soNetDiscountPerc") var soNetDiscountPerc: Double? = null,
    @SerializedName("soNetDiscount") var soNetDiscount: Double? = null,
    @SerializedName("soSubTotal") var soSubTotal: Double? = null,
    @SerializedName("soTax") var soTax: Double? = null,
    @SerializedName("soNetTotal") var soNetTotal: Double? = null,
    @SerializedName("soRoundingAmount") var soRoundingAmount: Double? = null,
    @SerializedName("otherCharges") var otherCharges: Double? = null,
    @SerializedName("soGrandTotal") var soGrandTotal: Double? = null,
    @SerializedName("salespersonId") var salespersonId: Int? = null,
    @SerializedName("deliveryPersonId") var deliveryPersonId: String? = null,
    @SerializedName("employeeId") var employeeId: String? = null,
    @SerializedName("soTotalValue") var soTotalValue: Double? = null,
    @SerializedName("profitMargin") var profitMargin: Double? = null,
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
    @SerializedName("paidAmount") var paidAmount: Double? = null,
    @SerializedName("balance") var balance: Double? = null,
    @SerializedName("usedAmount") var usedAmount: Double? = null,
    @SerializedName("fromLocationId") var fromLocationId: String? = null,
    @SerializedName("salesInvoiceId") var salesInvoiceId: String? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("fromLocationName") var fromLocationName: String? = null,
    @SerializedName("orderedQty") var orderedQty: Double? = null,
    @SerializedName("receivedQty") var receivedQty: Double? = null,
    @SerializedName("balanceQty") var balanceQty: Double? = null,
    @SerializedName("notified") var notified: Boolean? = null,
    @SerializedName("collected") var collected: Boolean? = null,
    @SerializedName("binLocation") var binLocation: String? = null,
    @SerializedName("isAutoPayment") var isAutoPayment: Boolean? = null,
    @SerializedName("paymentReference") var paymentReference: String? = null,
    @SerializedName("currencyId") var currencyId: String? = null,
    @SerializedName("currencyRate") var currencyRate: Int? = null,
    @SerializedName("isLuggageAccepted") var isLuggageAccepted: Boolean? = null,
    @SerializedName("module") var module: Int? = null,
    @SerializedName("poQuantity") var poQuantity: Double? = null,
    @SerializedName("orderedQuantity") var orderedQuantity: Double? = null,
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
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("paymentTermName") var paymentTermName: String? = null,
    @SerializedName("zatcaQRCode") var zatcaQRCode: String? = null,
    @SerializedName("isStockTransfer") var isStockTransfer: Boolean? = null,
    @SerializedName("reportName") var reportName: String? = null
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
            "I" -> "Invoiced"
            "A" -> "Approved"
            "H" -> "Partial"
            "R" -> "Delivered"
            "C" -> "Completed"
            "PA" -> "Paid"
            "RE" -> "Released"
            "RF" -> "Refunded"
            else -> "PENDING"

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