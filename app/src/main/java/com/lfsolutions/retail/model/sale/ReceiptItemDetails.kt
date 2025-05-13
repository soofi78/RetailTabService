package com.lfsolutions.retail.model.sale

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator


data class ReceiptItemDetails(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("salesReceiptId") var salesReceiptId: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("transactionAmount") var transactionAmount: Double? = null,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("actualAmount") var actualAmount: Double? = null,
    @SerializedName("balanceAmount") var balanceAmount: Double? = null,
    @SerializedName("applied") var applied: Boolean? = null,
    @SerializedName("appliedAmount") var appliedAmount: Double? = null,
    @SerializedName("roundingAmount") var roundingAmount: Double? = null,
    @SerializedName("strType") var strType: String? = null,
    @SerializedName("transactionDate") var transactionDate: String? = null,
    @SerializedName("dueDate") var dueDate: String? = null,
    @SerializedName("referenceNo") var referenceNo: String? = null,
    @SerializedName("slNo") var slNo: Int? = null,
    @SerializedName("transactionId") var transactionId: Int? = null

) : HistoryItemInterface {
    override fun getTitle(): String {
        return transactionNo.toString()
    }

    private fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            transactionDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndTime)
        return formatted ?: transactionDate ?: ""
    }

    override fun getDescription(): String {
        return getFormattedDate()
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + (amount?.formatDecimalSeparator() ?: "0.00")
    }

    override fun getSerializedNumber(): String {
        return slNo.toString()
    }

}