package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.SaleOrderInvoiceItem
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.DateTime


data class SaleOrderListItem(

    @SerializedName("soNo") var soNo: String? = null,
    @SerializedName("soDate") var soDate: String? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("salesOrderStatusId") var salesOrderStatusId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("soTotal") var soTotal: Int? = null,
    @SerializedName("soItemDiscount") var soItemDiscount: Int? = null,
    @SerializedName("soTotalValue") var soTotalValue: Int? = null,
    @SerializedName("soNetDiscountPerc") var soNetDiscountPerc: Int? = null,
    @SerializedName("soNetDiscount") var soNetDiscount: Int? = null,
    @SerializedName("soTotalAmount") var soTotalAmount: Int? = null,
    @SerializedName("soSubTotal") var soSubTotal: Int? = null,
    @SerializedName("soTax") var soTax: Int? = null,
    @SerializedName("soRoundingAmount") var soRoundingAmount: Int? = null,
    @SerializedName("soNetTotal") var soNetTotal: Int? = null,
    @SerializedName("soGrandTotal") var soGrandTotal: Int? = null,
    @SerializedName("fromLocationName") var fromLocationName: String? = null,
    @SerializedName("salesOrderStatus") var salesOrderStatus: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("creditDays") var creditDays: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("addOn") var addOn: String? = null,
    @SerializedName("deliveryPerson") var deliveryPerson: String? = null,
    @SerializedName("apiThirdPartyResponse") var apiThirdPartyResponse: String? = null,
    @SerializedName("isApiResponseSuccess") var isApiResponseSuccess: Boolean? = null,
    @SerializedName("isNetsuite") var isNetsuite: Boolean? = null,
    @SerializedName("erpInternalId") var erpInternalId: String? = null,
    @SerializedName("purchaseOrderId") var purchaseOrderId: String? = null,
    @SerializedName("orderStatus") var orderStatus: String? = null,
    @SerializedName("paidAmount") var paidAmount: Int? = null,
    @SerializedName("balance") var balance: Int? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("notified") var notified: String? = null,
    @SerializedName("collected") var collected: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("orderedQty") var orderedQty: Int? = null,
    @SerializedName("soNetCost") var soNetCost: Int? = null,
    @SerializedName("receivedQty") var receivedQty: Int? = null,
    @SerializedName("isBeep") var isBeep: Boolean? = null,
    @SerializedName("module") var module: String? = null,
    @SerializedName("paymentMethod") var paymentMethod: String? = null,
    @SerializedName("customerContact") var customerContact: String? = null,
    @SerializedName("customerEmail") var customerEmail: String? = null,
    @SerializedName("walkInCustomerDetail") var walkInCustomerDetail: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("isDelivered") var isDelivered: Boolean? = null,
    @SerializedName("isRefund") var isRefund: Boolean? = null,
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
        return (soNo ?: "") + " / " + (getFormattedDate())
    }

    private fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            soDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: soDate ?: ""
    }

    override fun getDescription(): String {
        return customerName ?: ""
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + soNetTotal?.formatDecimalSeparator()
    }
}