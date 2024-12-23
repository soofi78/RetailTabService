package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface


data class WorkAreaTypes(
    @SerializedName("displayText") var displayText: String? = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("isSelected") @Transient var isSelected: Boolean? = false,
) {
    override fun toString(): String {
        return displayText.toString()
    }
}