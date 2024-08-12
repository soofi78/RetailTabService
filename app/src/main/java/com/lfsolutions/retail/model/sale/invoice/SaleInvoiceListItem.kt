package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime


data class SaleInvoiceListItem(
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("invoiceDate") var invoiceDate: String? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("deliveryPerson") var deliveryPerson: String? = null,
    @SerializedName("invoiceTotal") var invoiceTotal: Double? = null,
    @SerializedName("invoiceItemDiscount") var invoiceItemDiscount: Double? = null,
    @SerializedName("invoiceTotalValue") var invoiceTotalValue: Double? = null,
    @SerializedName("invoiceNetDiscountPerc") var invoiceNetDiscountPerc: Double? = null,
    @SerializedName("invoiceNetDiscount") var invoiceNetDiscount: Double? = null,
    @SerializedName("invoiceTotalAmount") var invoiceTotalAmount: Double? = null,
    @SerializedName("invoiceSubTotal") var invoiceSubTotal: Double? = null,
    @SerializedName("invoiceTax") var invoiceTax: Double? = null,
    @SerializedName("invoiceRoundingAmount") var invoiceRoundingAmount: Double? = null,
    @SerializedName("invoiceNetTotal") var invoiceNetTotal: Double? = null,
    @SerializedName("invoiceGrandTotal") var invoiceGrandTotal: Double? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("customerRemarks") var customerRemarks: String? = null,
    @SerializedName("companyRemarks") var companyRemarks: String? = null,
    @SerializedName("creditDays") var creditDays: Double? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("balance") var balance: Double? = null,
    @SerializedName("paidAmount") var paidAmount: Double? = null,
    @SerializedName("isDirectInvoice") var isDirectInvoice: Boolean? = null,
    @SerializedName("salesOrderId") var salesOrderId: String? = null,
    @SerializedName("isPicked") var isPicked: String? = null,
    @SerializedName("memberName") var memberName: String? = null,
    @SerializedName("customerAddress") var customerAddress: String? = null,
    @SerializedName("customerPhoneNo") var customerPhoneNo: String? = null,
    @SerializedName("customerPostalCode") var customerPostalCode: String? = null,
    @SerializedName("employeeId") var employeeId: String? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("addOn") var addOn: String? = null,
    @SerializedName("memberId") var memberId: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("invoiceNetCost") var invoiceNetCost: Double? = null,
    @SerializedName("profitMargin") var profitMargin: Double? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null,
    @SerializedName("id") var id: Int? = null
) : HistoryItemInterface {
    override fun getTitle(): String {
        return (invoiceNo ?: "") + " / " + (getFormattedDate())
    }

    private fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            invoiceDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: invoiceDate ?: ""
    }

    override fun getDescription(): String {
        return customerName ?: ""
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + invoiceNetTotal?.formatDecimalSeparator()
    }
}