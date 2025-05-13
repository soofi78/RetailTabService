package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class CreateUpdateDriverMemoRequestBody(
    @SerializedName("driverMemo") var driverMemo: DriverMemo = DriverMemo(),
    @SerializedName("driverMemoDetail") var driverMemoDetail: ArrayList<DriverMemoDetail> = arrayListOf()
) {
    fun addEquipment(item: DriverMemoDetail) {
        driverMemoDetail.add(item)
        updatePriceAndQty()
    }

    fun updatePriceAndQty() {
        var totalPrice = 0.0
        var qty = 0.0
        driverMemoDetail.forEach {
            totalPrice += it.totalPrice ?: 0.0
            qty += it.qty ?: 0.0
        }

        driverMemo?.totalQty = qty
        driverMemo?.totalCost = totalPrice
    }

    fun serializeItems() {
        var serial = 0
        driverMemoDetail.forEach {
            serial += 1
            it.slNo = serial
        }
    }
}