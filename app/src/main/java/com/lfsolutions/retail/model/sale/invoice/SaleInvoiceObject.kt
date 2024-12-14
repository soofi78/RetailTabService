package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName


data class SaleInvoiceObject(

    @SerializedName("SalesInvoice", alternate = arrayOf("salesInvoice")) var salesInvoice: SalesInvoice? = SalesInvoice(),
    @SerializedName("SalesInvoiceDetail", alternate = arrayOf("salesInvoiceDetail")) var salesInvoiceDetail: ArrayList<SalesInvoiceDetail> = arrayListOf()

) {
    fun addEquipment(product: SalesInvoiceDetail) {
        salesInvoiceDetail.add(product)
        updatePriceAndQty()
    }

    fun updatePriceAndQty(dsc: Double = 0.0) {
        var qty = 0.0
        var netTotal = 0.0
        var discount = dsc
        var subTotal = 0.0
        var taxAmount = 0.0
        var total = 0.0

        salesInvoiceDetail.forEach {
            qty = qty.plus(it.qty ?: 0.0)
            netTotal = netTotal.plus(it.netTotal ?: 0.0)
            discount = discount.plus(it.netDiscount ?: 0.0)
            subTotal = subTotal.plus(it.subTotal ?: 0.0)
            taxAmount = taxAmount.plus(it.tax ?: 0.0)
            total = total.plus(it.totalValue?: 0.0)

        }
        salesInvoice?.invoiceQty = qty
        salesInvoice?.invoiceTotalValue = total
        salesInvoice?.invoiceNetTotal = netTotal
        salesInvoice?.invoiceSubTotal = subTotal
        salesInvoice?.invoiceTax = taxAmount
        salesInvoice?.invoiceGrandTotal = netTotal
        salesInvoice?.balance = netTotal
        salesInvoice?.netDiscount = discount
        salesInvoice?.invoiceNetDiscount = discount
        salesInvoice?.invoiceNetDiscountPerc =
            (salesInvoice?.invoiceGrandTotal?.let {
                discount.div(it)
            })?.times(100)


    }

    fun serializeItems() {
        var serial = 0
        salesInvoiceDetail.forEach {
            serial += 1
            it.slNo = serial
        }
    }
}