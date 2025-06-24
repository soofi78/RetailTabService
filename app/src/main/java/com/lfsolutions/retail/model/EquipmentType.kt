package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class EquipmentType(

    @SerializedName("value") var value: String? = null,
    @SerializedName("displayText") var displayText: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean? = null

) {
    override fun toString(): String {
        return displayText.toString()
    }

}