package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName


data class SaleInvoiceRequest (

  @SerializedName("salesInvoice"       ) var salesInvoice       : SalesInvoice?                 = SalesInvoice(),
  @SerializedName("salesInvoiceDetail" ) var salesInvoiceDetail : ArrayList<SalesInvoiceDetail> = arrayListOf()

)