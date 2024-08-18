package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class CreateUpdateAgreementMemoRequestBody(
    @SerializedName("AgreementMemo") var AgreementMemo: AgreementMemo? = AgreementMemo(),
    @SerializedName("AgreementMemoDetail") var AgreementMemoDetail: ArrayList<AgreementMemoDetail> = arrayListOf()
) {
    fun addEquipment(agreementMemoDetail: AgreementMemoDetail) {
        AgreementMemoDetail.add(agreementMemoDetail)
        updatePriceAndQty()
    }

    fun updatePriceAndQty() {
        var totalPrice = 0.0
        var qty = 0.0
        AgreementMemoDetail.forEach {
            totalPrice += it.TotalCost ?: 0.0
            qty += it.Qty ?: 0.0
        }

        AgreementMemo?.AgreementQty = qty
        AgreementMemo?.AgreementTotal = totalPrice
    }

    fun serializeItems() {
        var serial = 0
        AgreementMemoDetail.forEach {
            serial += 1
            it.SlNo = serial
        }
    }
}