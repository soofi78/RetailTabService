package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.formatDecimalSeparator
import kotlin.time.Duration.Companion.minutes


data class Product(
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("productId") var productId: Long? = null,
    @SerializedName("productName", alternate = arrayOf("name")) var productName: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("unitName") var unitName: String? = null,
    @SerializedName("unitId") var unitId: Int? = null,
    @SerializedName("qtyOnHand") var qtyOnHand: Double? = null,
    @SerializedName("qty") var qty: Double? = null,
    @SerializedName("cost", alternate = arrayOf("unitCost")) var cost: Double? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("isAsset") var isAsset: Boolean? = null,
    @SerializedName("applicableTaxes") var applicableTaxes: ArrayList<ApplicableTaxes>? = arrayListOf(),
    @SerializedName("type") var type: String? = null,
    @SerializedName("minimumQty") var minimumQty: Double? = null,
    @SerializedName("maximumQty") var maximumQty: Double? = null
) : HistoryItemInterface {
    override fun isSerialEquipment(): Boolean {
        return type.equals("S", true)
    }

    fun getApplicableTaxRate(): Int {
        if (applicableTaxes == null || applicableTaxes?.isEmpty() == true) return 0

        var tax = 0
        applicableTaxes?.forEach {
            tax += it.taxRate ?: 0
        }
        return tax
    }

    override fun getTitle(): String {
        return "$productName"
    }

    override fun getDescription(): String {
        return "Quantity: $qtyOnHand / $unitName"
    }

    fun getPrintQty(): String {
        return qtyOnHand?.formatDecimalSeparator().toString()
    }

    fun getPrintUOM(): String {
        return unitName?:""
    }

    fun getPrintMinQty(): String {
        return minimumQty?.formatDecimalSeparator().toString()
    }

    fun getPrintVarianceQty(): String {
        val varianceQty=qtyOnHand?.minus(minimumQty?:0.0)
        return varianceQty?.formatDecimalSeparator().toString()
    }

    override fun getAmount(): String {
        return Main.app.getSession().currencySymbol + price?.formatDecimalSeparator().toString()
    }

    override fun getMinQty(): String {
        return "Min. Qty: ${minimumQty?.formatDecimalSeparator().toString()}"
    }

    override fun getVarianceQty(): String {
        return "Variance Qty: ${qtyOnHand?.minus(minimumQty?:0.0)?.formatDecimalSeparator().toString()}"
    }


    override fun getImageUrl(): String {
        return (Main.app.getBaseUrl() + imagePath)
    }

    override fun getId(): Int {
        return productId?.toInt() ?: -1
    }
}