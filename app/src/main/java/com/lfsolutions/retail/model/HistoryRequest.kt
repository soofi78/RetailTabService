package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class HistoryRequest(
    @SerializedName("skipCount") var skipCount: Int = 0,
    @SerializedName("maxResultCount") var maxResultCount: Int = 50,
    @SerializedName("sorting") var sorting: String = "id Desc",
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("startDate") var startDate: String? = null,
    @SerializedName("endDate") var endDate: String? = null,
    @SerializedName("invoiceType") var invoiceType: String? = "X",
    @SerializedName("status") var status: String? = "A",
    @SerializedName("filter") var filter: String? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("referenceNo") var referenceNo: String? = null,
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("agreementNo") var agreementNo: Int? = null
)

