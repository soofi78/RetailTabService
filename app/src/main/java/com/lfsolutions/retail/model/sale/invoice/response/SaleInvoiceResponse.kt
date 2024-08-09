package com.lfsolutions.retail.model.sale.invoice.response

import com.google.gson.annotations.SerializedName


data class SaleInvoiceResponse(

    @SerializedName("salesInvoice") var salesInvoiceRes: SalesInvoiceRes? = SalesInvoiceRes(),
    @SerializedName("salesInvoiceDetail") var salesInvoiceDetailRes: ArrayList<SalesInvoiceDetailRes> = arrayListOf()

)