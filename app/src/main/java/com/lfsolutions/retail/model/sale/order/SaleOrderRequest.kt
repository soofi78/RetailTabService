package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName


data class SaleOrderRequest(
    @SerializedName("SalesOrder") var SalesOrder: SalesOrder? = SalesOrder(),
    @SerializedName("SalesOrderDetail") var SalesOrderDetail: ArrayList<SalesOrderDetail> = arrayListOf()

) {
    fun addEquipment(product: SalesOrderDetail) {
        SalesOrderDetail.add(product)
        updatePriceAndQty()
    }

    fun updatePriceAndQty(dsc: Double = 0.0) {
        var qty = 0
        var netTotal = 0.0
        var discount = dsc
        var subTotal = 0.0
        var taxAmount = 0.0
        var total = 0.0

        SalesOrderDetail.forEach {
            qty = qty.plus(it.Qty)
            netTotal = netTotal.plus(it.NetTotal)
            discount = discount.plus(it.NetDiscount)
            subTotal = subTotal.plus(it.SubTotal)
            taxAmount = taxAmount.plus(it.Tax)
            total = total.plus(it.TotalValue)

        }
        SalesOrder?.OrderedQty = qty
        SalesOrder?.OrderedQuantity = qty
        SalesOrder?.SoTotalValue = total
        SalesOrder?.SoNetTotal = netTotal
        SalesOrder?.SoSubTotal = subTotal
        SalesOrder?.SoTax = taxAmount
        SalesOrder?.SoGrandTotal = netTotal.minus(discount)
        SalesOrder?.Balance = netTotal.minus(discount)
        SalesOrder?.SoNetDiscount = discount
        SalesOrder?.SoNetDiscountPerc =
            SalesOrder?.SoGrandTotal?.let { discount.div(it).times(100) }


    }

    fun serializeItems() {
        var serial = 0
        SalesOrderDetail.forEach {
            serial += 1
            it.SlNo = serial
        }
    }
}
