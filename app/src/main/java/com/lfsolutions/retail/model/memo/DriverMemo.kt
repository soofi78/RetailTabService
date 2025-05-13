package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator


data class DriverMemo(
    @SerializedName("agreementNo") var agreementNo: String? = null,
    @SerializedName("agreementDate") var agreementDate: String? = null,
    @SerializedName("deliveryDate") var deliveryDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("totalQty") var totalQty: Double? = null,
    @SerializedName("totalCost") var totalCost: Double? = null,
    @SerializedName("driverTotalPrice") var driverTotalPrice: Double? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("driverMemoDetail") var driverMemoDetail: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null
) : HistoryItemInterface {
    override fun getTitle(): String {
        return (agreementNo ?: "") + " / " + (getFormattedDate())
    }

    fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            agreementDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: agreementDate ?: ""
    }

    fun getFormattedDeliverDate(): String {
        val date = DateTime.getDateFromString(
            deliveryDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: deliveryDate ?: ""
    }

    override fun getDescription(): String {
        return customerName ?: ""
    }

    override fun getAmount(): String {
        return "Qty: " + totalQty.toString().formatDecimalSeparator()
    }
}