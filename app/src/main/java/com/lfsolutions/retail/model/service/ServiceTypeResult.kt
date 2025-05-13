package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ServiceTypeResult (
  @SerializedName("items" ) var items : ArrayList<ServiceTypes> = arrayListOf()
)