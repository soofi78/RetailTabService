package com.lfsolutions.retail.model.profile

import com.google.gson.annotations.SerializedName


data class UserProfile(
    @SerializedName("profilePictureId") var profilePictureId: String? = null,
    @SerializedName("user") var user: User? = User(),
    @SerializedName("roles") var roles: ArrayList<Roles> = arrayListOf()
)