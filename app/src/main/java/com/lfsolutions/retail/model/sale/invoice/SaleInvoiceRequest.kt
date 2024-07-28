package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName


data class SaleInvoiceRequest(

    @SerializedName("SalesInvoice") var SalesInvoice: SalesInvoice? = SalesInvoice(),
    @SerializedName("SalesInvoiceDetail") var SalesInvoiceDetail: ArrayList<SalesInvoiceDetail> = arrayListOf()

) {
    fun addEquipment(product: SalesInvoiceDetail) {
        SalesInvoiceDetail.add(product)
        updatePriceAndQty()
    }

    fun updatePriceAndQty() {
        var qty = 0
        var netTotal = 0.0
        var discount = 0.0
        var subTotal = 0.0
        var taxAmount = 0.0
        var grandTotal = 0.0

        SalesInvoiceDetail.forEach {
            qty = qty.plus(it.Qty)
            netTotal = netTotal.plus(it.NetTotal)
            discount = discount.plus(it.NetDiscount)
            subTotal = subTotal.plus(it.SubTotal)
            taxAmount = taxAmount.plus(it.Tax)
            grandTotal = grandTotal.plus(it.TotalValue)

        }
        SalesInvoice?.InvoiceQty = qty
        SalesInvoice?.InvoiceTotalValue = netTotal
        SalesInvoice?.InvoiceNetDiscount = discount
        SalesInvoice?.InvoiceNetDiscount = discount
        SalesInvoice?.InvoiceSubTotal = subTotal
        SalesInvoice?.InvoiceTax = taxAmount
        SalesInvoice?.InvoiceGrandTotal = grandTotal
        SalesInvoice?.Balance = grandTotal

    }

    fun serializeItems() {
        var serial = 0
        SalesInvoiceDetail.forEach {
            serial += 1
            it.SlNo = serial
        }
    }
}