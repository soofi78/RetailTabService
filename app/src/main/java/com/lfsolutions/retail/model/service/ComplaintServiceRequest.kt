package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.memo.AgreementMemoDetail


data class ComplaintServiceRequest(
    @SerializedName("complaintService") var complaintService: ComplaintService? = ComplaintService(),
    @SerializedName("complaintServiceDetails") var complaintServiceDetails: ArrayList<ComplaintServiceDetails> = arrayListOf()
) {
    fun addEquipment(complaintServiceDetail: ComplaintServiceDetails) {
        complaintServiceDetails.add(complaintServiceDetail)
        updatePriceAndQty()
    }

    fun updatePriceAndQty() {
        var totalPrice = 0.0
        var qty = 0.0
        complaintServiceDetails.forEach {
            totalPrice += it.price
            qty += it.qty?.toDouble() ?: 0.0
        }

        complaintService?.totalQty = qty
        complaintService?.totalPrice = totalPrice
    }

    fun serializeItems() {
        var serial = 0
        complaintServiceDetails.forEach {
            serial += 1
            it.slNo = serial
        }
    }
}