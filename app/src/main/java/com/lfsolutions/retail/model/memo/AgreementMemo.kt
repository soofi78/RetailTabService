package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime

data class AgreementMemo(
    @SerializedName("Id", alternate = arrayOf("id")) var Id: Int? = null,
    @SerializedName(
        "AgreementNo",
        alternate = arrayOf("agreementNo")
    ) var AgreementNo: String? = null,
    @SerializedName(
        "AgreementDate",
        alternate = arrayOf("agreementDate")
    ) var AgreementDate: String? = null,
    @SerializedName("LocationId") var LocationId: Int? = null,
    @SerializedName(
        "AgreementQty",
        alternate = arrayOf("agreementQty")
    ) var AgreementQty: Double? = null,
    @SerializedName(
        "AgreementTotal",
        alternate = arrayOf("agreementTotal")
    ) var AgreementTotal: Double? = null,
    @SerializedName("Remarks") var Remarks: String? = null,
    @SerializedName("TenantId") var TenantId: Int? = 0,
    @SerializedName("Status", alternate = arrayOf("status")) var Status: String = "C",
    @SerializedName("CustomerId") var CustomerId: Int? = null,
    @SerializedName(
        "CustomerName",
        alternate = arrayOf("customerName")
    ) var CustomerName: String? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean = false,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: String? = null,
    @SerializedName("Signature") var Signature: String? = null
) : HistoryItemInterface {
    override fun getTitle(): String {
        return AgreementNo + " / " + transferDateFormatted()
    }

    fun transferDateFormatted(): String {
        val date = DateTime.getDateFromString(
            AgreementDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: AgreementDate ?: ""
    }

    override fun getDescription(): String {
        return CustomerName + " / " + Status
    }

    override fun getAmount(): String {
        return "Qty: " + AgreementQty.toString()
    }

}