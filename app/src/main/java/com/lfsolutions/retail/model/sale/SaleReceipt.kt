package com.lfsolutions.retail.model.sale

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator


data class SaleReceipt(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("receiptNo") var receiptNo: String? = null,
    @SerializedName("receiptDate") var receiptDate: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName(
        "paymentTypeName",
        alternate = arrayOf("paymentType")
    ) var paymentTypeName: String? = null,
    @SerializedName("paymentTypeId") var paymentTypeId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("customerCode") var customerCode: String? = null,
    @SerializedName("reference") var reference: String? = null,
    @SerializedName("createdByName") var createdByName: String? = null,
    @SerializedName(
        "receivedAmount",
        alternate = arrayOf("amount")
    ) var receivedAmount: Double? = null,
    @SerializedName("paymentDiscount") var discount: Double? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("erpInternalId") var erpInternalId: String? = null,
    @SerializedName("isPDCPayment") var isPDCPayment: Boolean? = null,
    @SerializedName("isNetsuite") var isNetsuite: Boolean? = null,
    @SerializedName("addOn") var addOn: String? = null,
    @SerializedName("apiThirdPartyResponse") var apiThirdPartyResponse: String? = null,
    @SerializedName("salesReceiptDetail") var salesReceiptDetail: String? = null,
    @SerializedName("items") var items: ArrayList<ReceiptItemDetails>? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("zatcaQRCode") var zatcaQRCode: String? = null,

    ) : HistoryItemInterface {


    override fun getTitle(): String {
        return receiptNo + " / " + getFormattedDate()
    }

    override fun getFormattedCreationTime():String{
        val formatted = DateTime.getFormattedSGTTime(creationTime)
        return formatted
    }

    fun getFormattedDate(): String {
        val date = DateTime.getDateFromString(
            receiptDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: receiptDate ?: ""
    }

    override fun getDescription(): String {
        return customerName.toString()
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + (receivedAmount?.formatDecimalSeparator()
            ?: "0.00")
    }


    fun discountFormatted(): String {
        return Main.app.getSession().currencySymbol + (discount?.formatDecimalSeparator() ?: "0.00")
    }

}