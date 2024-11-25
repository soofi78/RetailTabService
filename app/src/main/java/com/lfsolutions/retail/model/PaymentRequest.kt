package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(

    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("receiptNo") var receiptNo: String? = null,
    @SerializedName("receiptDate") var receiptDate: String? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("paymentTypeId") var paymentTypeId: Int? = null,
    @SerializedName("isCancelled") var isCancelled: Boolean? = false,
    @SerializedName("paymentDiscount") var paymentDiscount: Double? =0.0,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("reference") var reference: String? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("erpInternalId") var erpInternalId: String? = null,
    @SerializedName("currencyRate") var currencyRate: Int = 1,
    @SerializedName("salesOrderId") var salesOrderId: String? = null,
    @SerializedName("isPDCPayment") var isPDCPayment: Boolean? = null,
    @SerializedName("items") var items: ArrayList<CustomerSaleTransaction> = arrayListOf()

)