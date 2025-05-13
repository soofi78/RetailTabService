package com.lfsolutions.retail.model.dailysale

import com.google.gson.annotations.SerializedName


data class DailySalesItem(
    @SerializedName("paymentTerm") var paymentTerm: String? = null,
    @SerializedName("paymentTermId") var paymentTermId: Int? = null,
    @SerializedName("dailySalesInvoices") var dailySalesInvoices: ArrayList<DailySalesInvoices> = arrayListOf()
)