package com.lfsolutions.retail.model.dailysale

import com.google.gson.annotations.SerializedName


data class DailySalesInvoices(
    @SerializedName("invoiceNo") var invoiceNo: String? = null,
    @SerializedName("netTotal") var netTotal: Double? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("paymentTermId") var paymentTermId: String? = null
)