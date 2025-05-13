package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class Locations(
    @SerializedName("code") var code: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("companyName") var companyName: String? = null,
    @SerializedName("companyId") var companyId: Int? = null,
    @SerializedName("locationGroupId") var locationGroupId: String? = null,
    @SerializedName("menuId") var menuId: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("address3") var address3: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("fax") var fax: String? = null,
    @SerializedName("website") var website: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("isPurchaseAllowed") var isPurchaseAllowed: Boolean? = null,
    @SerializedName("isSalesAllowed") var isSalesAllowed: Boolean? = null,
    @SerializedName("remarks") var remarks: String? = null,
    @SerializedName("iswareHouse") var iswareHouse: Boolean? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: Int? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null
) {
    override fun toString(): String {
        return name.toString()
    }
}