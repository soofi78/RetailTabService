package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ReportTypeResult (
  @SerializedName("items" ) var items : ArrayList<ReportTypes> = arrayListOf()
)