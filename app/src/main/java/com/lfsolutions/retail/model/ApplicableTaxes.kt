package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class ApplicableTaxes(
    @SerializedName("taxId") var taxId: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("taxRate") var taxRate: Int? = null
) {
    override fun toString(): String {
        return name.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (name.equals((other as ApplicableTaxes).name)) {
            return true
        }
        return super.equals(other)
    }
}