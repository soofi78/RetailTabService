package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ComplaintServiceHistoryResult(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<ComplaintService> = arrayListOf()

)