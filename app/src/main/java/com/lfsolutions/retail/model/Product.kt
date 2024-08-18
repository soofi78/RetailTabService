package com.lfsolutions.retail.model

import com.github.gcacace.signaturepad.BuildConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class Product(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Long? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Double? = null,
    @SerializedName("cost") var cost: Double? = null,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes>? = arrayListOf(),
    @SerializedName("type") var type: String? = null
) {
    fun isSerialEquipment(): Boolean {
        return type.equals("S", true)
    }

    fun getApplicableTaxRate(): Int {
        if (applicableTaxes == null || applicableTaxes?.isEmpty() == true)
            return 0

        var tax = 0
        applicableTaxes?.forEach {
            tax += it.taxRate ?: 0
        }
        return tax
    }
}