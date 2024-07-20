package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ApplicableTaxes (

  @SerializedName("taxId"   ) var taxId   : Int?    = null,
  @SerializedName("name"    ) var name    : String? = null,
  @SerializedName("taxRate" ) var taxRate : Int?    = null

)