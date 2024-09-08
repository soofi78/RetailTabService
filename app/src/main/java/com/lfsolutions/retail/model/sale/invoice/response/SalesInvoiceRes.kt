package com.lfsolutions.retail.model.sale.invoice.response

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime


data class SalesInvoiceRes(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("invoiceDate") var invoiceDate: String? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("deliveryTimingDescription") var deliveryTimingDescription: String? = null,
    @SerializedName("customerRemarks") var customerRemarks: String? = null,
    @SerializedName("companyRemarks") var companyRemarks: String? = null,
    @SerializedName("balance") var balance: Double? = null,
    @SerializedName("invoiceItemDiscount") var invoiceItemDiscount: Double? = null,
    @SerializedName("invoiceNetDiscountPerc") var invoiceNetDiscountPerc: Double? = null,
    @SerializedName("invoiceNetDiscount") var invoiceNetDiscount: Double? = null,
    @SerializedName("invoiceSubTotal") var invoiceSubTotal: Double? = null,
    @SerializedName("invoiceTax") var invoiceTax: Double? = null,
    @SerializedName("invoiceNetTotal") var invoiceNetTotal: Double? = null,
    @SerializedName("invoiceRoundingAmount") var invoiceRoundingAmount: Double? = null,
    @SerializedName("otherCharges") var otherCharges: Double? = null,
    @SerializedName("netDiscount") var netDiscount: Double? = null,
    @SerializedName("invoiceGrandTotal") var invoiceGrandTotal: Double? = null,
    @SerializedName("invoiceTotalValue") var invoiceTotalValue: Double? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("creditDays") var creditDays: Int? = null,
    @SerializedName("salesOrderId") var salesOrderId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("salespersonId") var salespersonId: Int? = null,
    @SerializedName("deliveryPersonId") var deliveryPersonId: String? = null,
    @SerializedName("deliveryPersonName") var deliveryPersonName: String? = null,
    @SerializedName("salesPersonName") var salesPersonName: String? = null,
    @SerializedName("amountInWords") var amountInWords: String? = null,
    @SerializedName("paymentTermId") var paymentTermId: Int? = null,
    @SerializedName("branchId") var branchId: String? = null,
    @SerializedName("paymentTermName") var paymentTermName: String? = null,
    @SerializedName("fromLocationName") var fromLocationName: String? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("tokenId") var tokenId: String? = null,
    @SerializedName("invoiceDueDate") var invoiceDueDate: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("profitMargin") var profitMargin: Int? = null,
    @SerializedName("taxName") var taxName: String? = null,
    @SerializedName("signature") var signature: String? = null,
    @SerializedName("deliveryOrderList") var deliveryOrderList: String? = null,
    @SerializedName("deliveryOrdered") var deliveryOrdered: String? = null,
    @SerializedName("deliveryOrderDate") var deliveryOrderDate: String? = null,
    @SerializedName("deliveryTimingId") var deliveryTimingId: String? = null,
    @SerializedName("memberId") var memberId: String? = null,
    @SerializedName("vendorId") var vendorId: Int? = null,
    @SerializedName("paidAmount") var paidAmount: Double? = null,
    @SerializedName("currencyId") var currencyId: String? = null,
    @SerializedName("currencyRate") var currencyRate: Double? = null,
    @SerializedName("zatcaQRCode") var zatcaQRCode: String? = null,
    @SerializedName("thirdPartyId") var thirdPartyId: String? = null,
    @SerializedName("autoSync") var autoSync: Boolean? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: Int? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null

) {
    fun InvoiceDateFormatted(): String {
        val date = DateTime.getDateFromString(
            invoiceDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: invoiceDate ?: ""
    }

    fun StatusFormatted(): String {
        return when (status) {
            "I" -> "INVOICED"
            "P" -> "PAID"
            "D" -> "DRAFT"
            else -> status.toString()
        }
    }

    fun InvoiceNetTotalFromatted(): String {
        return Main.app.getSession().currencySymbol + invoiceNetTotal?.formatDecimalSeparator()
    }

    fun BalanceFormatted(): String {
        return Main.app.getSession().currencySymbol + balance?.formatDecimalSeparator()
    }

    fun signatureUrl(): String {
        return Main.app.getBaseUrl() + signature
    }
}