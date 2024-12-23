package com.lfsolutions.retail.model.service

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("id") var id: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("values") var values: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("selected") var selected: String? = null,
    @SerializedName("active") var active: Boolean? = null,
    @SerializedName("valueList") var feedbackValue: ArrayList<FeedbackValue> = arrayListOf(),
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null
)