package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class Customer(
    @SerializedName("customerCode") var customerCode: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("address3") var address3: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("postalCode") var postalCode: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phoneNo") var phoneNo: String? = null,
    @SerializedName("faxNo") var faxNo: String? = null,
    @SerializedName("group") var group: String? = null,
    @SerializedName("area") var area: String? = null,
    @SerializedName("creationTimes") var creationTimes: String? = null,
    @SerializedName("paymentTermId") var paymentTermId: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("selected") @Transient var isSelected: Boolean = false
)