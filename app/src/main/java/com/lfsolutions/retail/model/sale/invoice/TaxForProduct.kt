package com.lfsolutions.retail.model.sale.invoice

import com.google.gson.annotations.SerializedName


data class TaxForProduct(

    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("taxId") var taxId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("taxRate") var taxRate: Int? = null,
    @SerializedName("taxValue") var taxValue: Int? = null

)