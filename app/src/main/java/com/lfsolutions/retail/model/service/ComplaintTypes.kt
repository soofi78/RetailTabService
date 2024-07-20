package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface


data class ComplaintTypes(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("isSelected") @Transient var isSelected: Boolean? = false,
    @SerializedName("productId") var productId: Int? = null,
) : MultiSelectModelInterface {
    override fun getId(): Long {
        return id?.toLong() ?: 0
    }

    override fun getText(): String {
        return name ?: ""
    }

    override fun isSelected(): Boolean {
        return isSelected ?: false
    }

    override fun setSelected(selected: Boolean) {
        isSelected = selected
    }

}