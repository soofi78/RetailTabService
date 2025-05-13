package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.DateTime


data class SaleOrderListItem(

    @SerializedName("soNo") var soNo: String? = null,
    @SerializedName("soDate") var soDate: String? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("salesOrderStatusId") var salesOrderStatusId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("soTotal") var soTotal: Int? = null,
    @SerializedName("soItemDiscount") var soItemDiscount: Double? = null,
    @SerializedName("soTotalValue") var soTotalValue: Double? = null,
    @SerializedName("soNetDiscountPerc") var soNetDiscountPerc: Double? = null,
    @SerializedName("soNetDiscount") var soNetDiscount: Double? = null,
    @SerializedName("soTotalAmount") var soTotalAmount: Double? = null,
    @SerializedName("soSubTotal") var soSubTotal: Double? = null,
    @SerializedName("soTax") var soTax: Double? = null,
    @SerializedName("soRoundingAmount") var soRoundingAmount: Double? = null,
    @SerializedName("soNetTotal") var soNetTotal: Double? = null,
    @SerializedName("soGrandTotal") var soGrandTotal: Double? = null,
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
    @SerializedName("paidAmount") var paidAmount: Double? = null,
    @SerializedName("balance") var balance: Double? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("notified") var notified: String? = null,
    @SerializedName("collected") var collected: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("orderedQty") var orderedQty: Int? = null,
    @SerializedName("soNetCost") var soNetCost: Double? = null,
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
    @SerializedName("profitMargin") var profitMargin: Double? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null,
    @SerializedName("isStockTransfer") var isStockTransfer: Boolean? = null,
    @SerializedName("id") var id: Int? = null

) : HistoryItemInterface {
    override fun getTitle(): String {
        return (soNo ?: "") + " / " + (getFormattedDate())
    }

    private fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            soDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: soDate ?: ""
    }

    override fun getId(): Int {
        return id ?: -1
    }

    override fun getDescription(): String {
        return (customerName ?: "") + "\nPO No. " + (poNo ?: "")
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + soNetTotal?.formatDecimalSeparator()
    }
}