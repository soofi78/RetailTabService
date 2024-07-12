package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class EquipmentTypeResult(
    @SerializedName("items") var items: List<EquipmentType> = arrayListOf()
)