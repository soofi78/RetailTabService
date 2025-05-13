package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class CustomerSaleTransaction(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("strType") var strType: String? = null,
    @SerializedName("transactionDate") var transactionDate: String? = null,
    @SerializedName("dueDate") var dueDate: String? = null,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("currencyRate") var currencyRate: Int = 1,
    @SerializedName("actualAmount") var actualAmount: Double? = null,
    @SerializedName("balanceAmount") var balanceAmount: Double? = null,
    @SerializedName("roundingAmount") var roundingAmount: Double? = null,
    @SerializedName("referenceNo") var referenceNo: String? = null,
    @SerializedName("transactionId") var transactionId: Int? = null,
    @SerializedName("applied") var applied: Boolean? = null,
    @SerializedName("appliedAmount") var appliedAmount: Double? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (id == (other as CustomerSaleTransaction).id) {
            return true
        }
        if (transactionNo == (other as CustomerSaleTransaction).transactionNo) {
            return true
        }
        return super.equals(other)
    }
}