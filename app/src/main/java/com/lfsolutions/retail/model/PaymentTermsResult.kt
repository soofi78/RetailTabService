package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class PaymentTermsResult (

  @SerializedName("items" ) var items : ArrayList<PaymentTerm> = arrayListOf()

)