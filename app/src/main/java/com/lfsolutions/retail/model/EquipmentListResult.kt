package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class EquipmentListResult(
    @SerializedName("items") var items: ArrayList<Product> = arrayListOf()
)