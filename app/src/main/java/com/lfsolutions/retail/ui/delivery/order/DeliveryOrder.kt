package com.lfsolutions.retail.ui.delivery.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.DateTime

data class DeliveryOrder(
    var creationTime: String? = null,
    var creatorUserId: Int? = null,
    var currencyId: Int? = null,
    var currencyRate: Int? = 0,
    var customerId: Int? = null,
    var customerName: String? = null,
    var customerCode: String? = null,
    var deleterUserId: Int? = null,
    var deletionTime: String? = null,
    var deliveryDate: String? = null,
    var deliveryNo: String? = null,
    var deliveryPersonId: String? = null,
    var deliveryTimingId: String? = null,
    var erpInternalId: Int? = null,
    var id: Int? = null,
    var isCancelled: Boolean? = null,
    var isDeleted: Boolean? = null,
    var isFoc: Boolean? = false,
    var lastModificationTime: String? = null,
    var lastModifierUserId: Int? = null,
    var locationId: Int? = null,
    var salesPersonId: Int? = null,
    var memberId: Int? = null,
    var paidAmount: Double? = 0.0,
    var poOtherCharges: Double? = 0.0,
    var poRoundingAmount: Double? = 0.0,
    var referenceNo: String? = null,
    var remarks: String? = null,
    var salesInvoiceId: Int? = null,
    var salesOrderId: Int? = null,
    var soNetDiscount: Double? = 0.0,
    var status: String? = "R",
    var tenantId: Int? = 0,
    var totalDeliveredQty: Double? = null,
    var totalQty: Double? = 0.0,
    var signature: String? = null,
    var subTotal: Double? = null,
    var address1: String? = null,
    var address2: String? = null,
    var zatcaQRCode: String? = null,
    var reportName: String? = null,
    @SerializedName("CustomerServiceToVisitId", alternate = arrayOf("customerServiceToVisitId")) var customerServiceToVisitId: Long= 0,
) {

    fun signatureUrl(): String {
        return Main.app.getBaseUrl() + signature
    }

    fun DeliveryDateFormatted(): String {
        val date = DateTime.getDateFromString(
            deliveryDate,
            DateTime.DateTimetRetailFormat.replace("T", " ").replace("Z", "")
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: deliveryDate ?: ""
    }

    fun statusFormatted(): String {
        return when (status?.trim()) {
            "R" -> "Delivered"
            "D" -> "Draft"
            "I" -> "Invoiced"
            else -> "Unknown"
        }
    }
}