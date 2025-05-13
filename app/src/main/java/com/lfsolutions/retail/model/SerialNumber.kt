package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface

data class SerialNumber(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("isBatch") var isBatch: Boolean? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("serialNumber") var serialNumber: String? = null,
    @SerializedName("batchCode") var batchCode: String? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int? = null,
    @SerializedName("qty") var qty: Int? = null,
    @SerializedName("mfgDate") var mfgDate: String? = null,
    @SerializedName("expiryDate") var expiryDate: String? = null,
    @SerializedName("productBatchLocationId") var productBatchLocationId: Int? = null,
    @SerializedName("isSelected") var isSelected: Boolean? = null,
    @SerializedName("style") var style: String? = null,
    @SerializedName("unitCost") var unitCost: Int? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("moduleName") var moduleName: String? = null,
    @SerializedName("module") var module: Int? = null

) : MultiSelectModelInterface {
    override fun getId(): Long {
        return id?.toLong() ?: 0
    }

    override fun getText(): String {
        return serialNumber ?: ""
    }

    override fun isSelected(): Boolean {
        return isSelected ?: false
    }

    override fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    override fun equals(other: Any?): Boolean {
        if (id?.equals((other as SerialNumber).id) == true)
            return true
        return super.equals(other)
    }

}