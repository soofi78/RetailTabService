package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName


data class FeedbackTypeResult (
  @SerializedName("items" ) var items : ArrayList<FeedbackTypes> = arrayListOf()
)