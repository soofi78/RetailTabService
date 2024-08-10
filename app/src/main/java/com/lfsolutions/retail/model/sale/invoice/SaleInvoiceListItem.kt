package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.SaleOrderInvoiceItem
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
    @SerializedName("invoiceTotal") var invoiceTotal: Int? = null,
    @SerializedName("invoiceItemDiscount") var invoiceItemDiscount: Int? = null,
    @SerializedName("invoiceTotalValue") var invoiceTotalValue: Int? = null,
    @SerializedName("invoiceNetDiscountPerc") var invoiceNetDiscountPerc: Int? = null,
    @SerializedName("invoiceNetDiscount") var invoiceNetDiscount: Int? = null,
    @SerializedName("invoiceTotalAmount") var invoiceTotalAmount: Int? = null,
    @SerializedName("invoiceSubTotal") var invoiceSubTotal: Int? = null,
    @SerializedName("invoiceTax") var invoiceTax: Int? = null,
    @SerializedName("invoiceRoundingAmount") var invoiceRoundingAmount: Int? = null,
    @SerializedName("invoiceNetTotal") var invoiceNetTotal: Int? = null,
    @SerializedName("invoiceGrandTotal") var invoiceGrandTotal: Int? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("customerRemarks") var customerRemarks: String? = null,
    @SerializedName("companyRemarks") var companyRemarks: String? = null,
    @SerializedName("creditDays") var creditDays: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("balance") var balance: Int? = null,
    @SerializedName("paidAmount") var paidAmount: Int? = null,
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
    @SerializedName("invoiceNetCost") var invoiceNetCost: Int? = null,
    @SerializedName("profitMargin") var profitMargin: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null,
    @SerializedName("id") var id: Int? = null
) : SaleOrderInvoiceItem {
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