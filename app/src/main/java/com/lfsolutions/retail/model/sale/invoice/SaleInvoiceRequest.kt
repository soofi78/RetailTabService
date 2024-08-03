package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main


data class SaleInvoiceRequest(

    @SerializedName("SalesInvoice") var SalesInvoice: SalesInvoice? = SalesInvoice(),
    @SerializedName("SalesInvoiceDetail") var SalesInvoiceDetail: ArrayList<SalesInvoiceDetail> = arrayListOf()

) {
    fun addEquipment(product: SalesInvoiceDetail) {
        SalesInvoiceDetail.add(product)
        updatePriceAndQty()
    }

    fun updatePriceAndQty(dsc: Double = 0.0) {
        var qty = 0
        var netTotal = 0.0
        var discount = dsc
        var subTotal = 0.0
        var taxAmount = 0.0
        var total = 0.0

        SalesInvoiceDetail.forEach {
            qty = qty.plus(it.Qty)
            netTotal = netTotal.plus(it.NetTotal)
            discount = discount.plus(it.NetDiscount)
            subTotal = subTotal.plus(it.SubTotal)
            taxAmount = taxAmount.plus(it.Tax)
            total = total.plus(it.TotalValue)

        }
        SalesInvoice?.InvoiceQty = qty
        SalesInvoice?.InvoiceTotalValue = total
        SalesInvoice?.InvoiceNetTotal = netTotal
        SalesInvoice?.InvoiceSubTotal = subTotal
        SalesInvoice?.InvoiceTax = taxAmount
        SalesInvoice?.InvoiceGrandTotal = netTotal.minus(discount)
        SalesInvoice?.Balance = netTotal.minus(discount)
        SalesInvoice?.NetDiscount = discount
        SalesInvoice?.InvoiceNetDiscount = discount
        SalesInvoice?.InvoiceNetDiscountPerc =
            (SalesInvoice?.InvoiceGrandTotal?.let {
                discount.div(it)
            })?.times(100)


    }

    fun serializeItems() {
        var serial = 0
        SalesInvoiceDetail.forEach {
            serial += 1
            it.SlNo = serial
        }
    }
}