package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class ActionTypeResult (
  @SerializedName("items" ) var items : ArrayList<ActionType> = arrayListOf()

)