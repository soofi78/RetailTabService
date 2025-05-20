package com.lfsolutions.retail.model.product

import com.google.gson.annotations.SerializedName


data class Asset(

    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("locationId") var locationId: Int? = null,
    @SerializedName("productBatchId") var productBatchId: Int? = null,
    @SerializedName("serialNumber") var serialNumber: String? = null,
    @SerializedName("customerId") var customerId: Int? = null,
    @SerializedName("disposeDate") var disposeDate: String? = null,
    @SerializedName("depDate") var depDate: String? = null,
    @SerializedName("purchaseDate") var purchaseDate: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("purchaseCost") var purchaseCost:Double? = null,
    @SerializedName("depRate") var depRate: Double? = null,
    @SerializedName("depreciationAmount") var depreciationAmount: Double? = null,
    @SerializedName("bookValue") var bookValue: Double? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("locationName") var locationName: String? = null,
    @SerializedName("inventoryCode") var inventoryCode: String? = null,
    @SerializedName("customerName") var customerName: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null,

    @Transient
    var isProductChecked: Boolean = false
)