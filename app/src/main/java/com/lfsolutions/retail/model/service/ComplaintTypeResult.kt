package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ComplaintTypeResult (
  @SerializedName("items" ) var items : ArrayList<ComplaintTypes> = arrayListOf()

)