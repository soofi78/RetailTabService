package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator

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
        alternate = ["agreementTotal"]
    ) var AgreementTotal: Double? = null,
    @SerializedName("Remarks", alternate = ["remarks"]) var Remarks: String? = null,
    @SerializedName("TenantId", alternate = ["tenantId"]) var TenantId: Int? = 0,
    @SerializedName("Status", alternate = ["status"]) var Status: String = "C",
    @SerializedName("CustomerId", alternate = ["customerId"]) var CustomerId: Int? = null,
    @SerializedName("CustomerName", alternate = arrayOf("customerName")) var CustomerName: String? = null,
    @SerializedName("CustomerCode", alternate = arrayOf("customerCode")) var customerCode: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean = false,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: Int? = null,
    @SerializedName("Signature", alternate = arrayOf("signature")) var Signature: String? = null,
    @SerializedName("customerFeedbackList") var customerFeedbackList: ArrayList<Feedback> = arrayListOf(),
) : HistoryItemInterface {
    override fun getTitle(): String {
        return AgreementNo + " / " + agreementDateFormatted()
    }

    override fun getFormattedCreationTime():String{
        val formatted = DateTime.getFormattedSGTTime(creationTime)
        return formatted
    }

    fun agreementDateFormatted(): String {
        val date = DateTime.getDateFromString(
            AgreementDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: AgreementDate ?: ""
    }

    override fun getDescription(): String {
        return CustomerName + " / " + Status
    }

    override fun getAmount(): String {
        return "Qty: " + AgreementQty.toString()
    }

    override fun getMinQty(): String {
        return ""
    }

    fun totalFormatted(): String {
        return Main.app.getSession().currencySymbol + "" + AgreementTotal?.formatDecimalSeparator()
    }

    fun signatureUrl(): String {
        return AppSession.get(Constants.baseUrl) + Signature
    }
}