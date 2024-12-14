package com.lfsolutions.retail.model.dailysale

import com.google.gson.annotations.SerializedName


data class DailySaleRecord(
    @SerializedName("netTotal") var netTotal: Double? = null,
    @SerializedName("dailySalesPaymentTermListDto") var dailySalesItem: ArrayList<DailySalesItem> = arrayListOf()
)