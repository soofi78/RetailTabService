package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ComplaintService(
    @SerializedName("id") var id: String? = null,
    @SerializedName("csNo") var csNo: String? = null,
    @SerializedName("csDate") var csDate: String? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("status") var status: String? = "C",
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("totalQty") var totalQty: Int? = null,
    @SerializedName("totalPrice") var totalPrice: Double = 0.0,
    @SerializedName("timeIn") var timeIn: String? = null,
    @SerializedName("timeOut") var timeOut: String? = null,
    @SerializedName("complaintBy") var complaintBy: String? = null,
    @SerializedName("designation") var designation: String? = null,
    @SerializedName("mobileNo") var mobileNo: String? = null,
    @SerializedName("interval") var interval: Int = 90,
    @SerializedName("customerFeedback") var customerFeedback: String? = null,
    @SerializedName("signature") var signature: String? = null,
    @SerializedName("customerFeedbackList") var customerFeedbackList: ArrayList<Feedback> = arrayListOf(),
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("date") var date: String? = null
)