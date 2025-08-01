package com.lfsolutions.retail.model.outgoingstock

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime


data class StockTransfer(

    @SerializedName("transferNo") var transferNo: String? = null,
    @SerializedName("transferDate") var transferDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("toLocationId") var toLocationId: Int? = null,
    @SerializedName("locationName") var locationName: String? = null,
    @SerializedName("toLocationName") var toLocationName: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("transferQty") var transferQty: Double? = null,
    @SerializedName("transferTotal") var transferTotal: Double? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("transferTotalPrice") var transferTotalPrice: Double? = null,
    @SerializedName("isMatrix") var isMatrix: Boolean? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("isPosted") var isPosted: Boolean? = null,
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
        return transferNo + " / " + transferDateFormatted()
    }

    override fun getFormattedCreationTime():String{
        val formatted = DateTime.getFormattedSGTTime(creationTime)
        return formatted
    }

    fun transferDateFormatted(): String {
        val date = DateTime.getDateFromString(
            transferDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: transferDate ?: ""
    }

    override fun getDescription(): String {
        return toLocationName + " / " + status
    }

    override fun getAmount(): String {
        return "Qty: " + transferQty.toString()
    }

    fun statusFormatted(): String {
        return when (status) {
            "R" -> "Received"
            "T" -> "Transferred"
            else -> ""
        }
    }

}