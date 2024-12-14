package com.lfsolutions.retail.model.deliveryorder

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime


data class DeliveryOrderHistoryItem(

    @SerializedName("deliveryNo") var deliveryNo: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("deliveryDate") var deliveryDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("employeeName") var employeeName: String? = null,
    @SerializedName("addon") var addon: String? = null,
    @SerializedName("deliveryPerson") var deliveryPerson: String? = null,
    @SerializedName("isApiResponseSuccess") var isApiResponseSuccess: Boolean? = null,
    @SerializedName("isNetsuite") var isNetsuite: Boolean? = null,
    @SerializedName("erpInternalId") var erpInternalId: String? = null,
    @SerializedName("referenceNo") var referenceNo: String? = null,
    @SerializedName("deliveryOrderId") var deliveryOrderId: Int? = null,
    @SerializedName("poNo") var poNo: String? = null,
    @SerializedName("soNo") var soNo: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("deliveryTiming") var deliveryTiming: String? = null,
    @SerializedName("salesInvoiceNo") var salesInvoiceNo: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("customerAddress") var customerAddress: String? = null,
    @SerializedName("customerPostalCode") var customerPostalCode: String? = null,
    @SerializedName("customerPhoneNo") var customerPhoneNo: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null

) : HistoryItemInterface {
    override fun getTitle(): String {
        return deliveryNo.toString() + " / " + (getFormattedDate())
    }

    private fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            deliveryDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: deliveryDate ?: ""
    }

    override fun getId(): Int {
        return id ?: -1
    }

    override fun getDescription(): String {
        return customerName ?: ""
    }

    override fun getAmount(): String {
        return status.toString()
    }
}