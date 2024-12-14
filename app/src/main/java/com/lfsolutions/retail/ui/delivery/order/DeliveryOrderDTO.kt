package com.lfsolutions.retail.ui.delivery.order

data class DeliveryOrderDTO(
    var deliveryOrder: DeliveryOrder = DeliveryOrder(),
    var deliveryOrderDetail: ArrayList<DeliveryOrderDetails> = ArrayList()
) {
    fun serializeItems() {
        var serial = 0
        deliveryOrderDetail.forEach {
            serial += 1
            it.slNo = serial
        }
    }

    fun addEquipment(deliveryOrderDetails: DeliveryOrderDetails) {
        this.deliveryOrderDetail.add(deliveryOrderDetails)
        updatePriceAndQty()
    }

    fun updatePriceAndQty() {
        var qty = 0.0
        var cost = 0.0

        deliveryOrderDetail.forEach {
            qty = qty.plus(it.deliverQty ?: 0.0)
        }

        deliveryOrder.totalQty = 0.0
        deliveryOrder.totalDeliveredQty = qty
        deliveryOrder.subTotal = 0.0

    }
}