package com.lfsolutions.retail.model.deliveryorder

import com.google.gson.annotations.SerializedName


data class DeliveryOrderHistoryList (

  @SerializedName("totalCount" ) var totalCount : Int?             = null,
  @SerializedName("items"      ) var items      : ArrayList<DeliveryOrderHistoryItem> = arrayListOf()

)