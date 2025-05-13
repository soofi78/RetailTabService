package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CategoryItem(
    @SerializedName("name") var name: String? = null,
    @SerializedName("code") var code: String? = null,
    @SerializedName("categoryImage") var categoryImage: String? = null,
    @SerializedName("monthlyQtyLimit") var monthlyQtyLimit: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: Int? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("isSelected") @Transient var isSelected: Boolean = false
)