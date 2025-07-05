package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.product.Asset
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime


data class ComplaintService(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("csNo") var csNo: String? = null,
    @SerializedName("csDate") var csDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("customerCode") var customerCode: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("status") var status: String? = "C",
    @SerializedName("type") var type: String? = "C",
    @SerializedName("reportType") var reportType: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("totalQty") var totalQty: Double? = 0.0,
    @SerializedName("totalPrice") var totalPrice: Double = 0.0,
    @SerializedName("TimeIn", alternate = arrayOf("time in","timeIn","Time In")) var timeIn: String? = null,//2025-06-13T11:54:46Z
    @SerializedName("timeInStr", alternate = arrayOf("time inStr","TimeInStr","Time InStr")) val timeInStr: String? = null,
    @SerializedName("TimeOut", alternate = arrayOf("time out","timeOut" ,"Time Out")) var timeOut: String? = null,
    @SerializedName("timeOutStr", alternate = arrayOf("time outStr","TimeOutStr" ,"Time OutStr")) val timeOutStr: String? = null,
    @SerializedName("complaintBy") var complaintBy: String? = null,
    @SerializedName("designation") var designation: String? = null,
    @SerializedName("mobileNo") var mobileNo: String? = null,
    @SerializedName("interval") var interval: Int = 90,
    @SerializedName("customerFeedback") var customerFeedback: String? = null,
    @SerializedName("signature") var signature: String? = null,
    @SerializedName("customerFeedbackList") var customerFeedbackList: ArrayList<Feedback> = arrayListOf(),
    @SerializedName("CustomerAssetsAllocationList", alternate = arrayOf("customerAssetsAllocationList")) var customerAssetsAllocationList: ArrayList<Asset> = arrayListOf(),
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("CustomerServiceToVisitId", alternate = arrayOf("customerServiceToVisitId")) var customerServiceToVisitId: Long= 0,
    var csDateTime: String? = null,
) : HistoryItemInterface {

     fun getCheckInTime():String{
        return if(!timeInStr.isNullOrBlank()){
            timeInStr
         }else{
            DateTime.getTimeFromDateString(timeIn)
         }
     }

    fun getCheckOutTime():String{
        return if(!timeOutStr.isNullOrBlank()){
            timeOutStr
        }else{
            DateTime.getTimeFromDateString(timeOut)
        }
    }
    override fun getTitle(): String {
        return csNo + " / " + serviceDateFormatted()
    }

    override fun getFormattedCreationTime():String{
        val formatted = DateTime.getFormattedSGTTime(creationTime)
        return formatted
    }

    fun serviceDateFormatted(): String {
        val date = DateTime.getDateFromString(
            csDate?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: csDate ?: ""
    }

    override fun getDescription(): String {
        return customerName + " / " + status
    }

    override fun getAmount(): String {
        return "Qty: " + totalQty.toString()
    }

    fun statusFormatted(): String {
        return if (status.equals("C", true)) {
            "Completed"
        } else {
            "Pending"
        }
    }

    fun signatureUrl(): String {
        return AppSession[Constants.baseUrl] + signature
    }

    fun totalFormatted(): String {
        return Main.app.getSession().currencySymbol + totalPrice
    }
}