package com.lfsolutions.retail.model.outgoingstock

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.ApplicableTaxes
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface


data class OutGoingProduct(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Long? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Int = 0,
    @SerializedName("cost") var cost: Int = 0,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("price") var price: Int = 0,
    @SerializedName("subTotal") var subTotal: Int = 0,
    @SerializedName("qty") var qty: Int = 0,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes> = arrayListOf(),
    @SerializedName("productBatchList") var productBatchList: ArrayList<ProductBatchList> = arrayListOf(),
    @SerializedName("unit") var unit: String? = null,
) {
    fun getSerialNumbers(): String {
        var serials = ""
        productBatchList.forEach {
            Log.d("Serial", it.SerialNumber.toString())
            serials += if (serials.equals("")) it.SerialNumber.toString() else "\n" + it.SerialNumber
        }
        return serials
    }

    fun getPreSelectedSerialNumbers(serialNumbersList: ArrayList<SerialNumber>): ArrayList<out MultiSelectModelInterface>? {
        val serials = ArrayList<MultiSelectModelInterface>()
        productBatchList.forEach {
            val index = serialNumbersList.indexOf(SerialNumber(id = it.Id))
            if (index > -1) {
                serials.add(serialNumbersList[index])
            }
        }
        return serials
    }
}

