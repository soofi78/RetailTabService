package com.lfsolutions.retail.model.profile

import com.google.gson.annotations.SerializedName


data class User(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("surname") var surname: String? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("emailAddress") var emailAddress: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("isActive") var isActive: Boolean? = null,
    @SerializedName("shouldChangePasswordOnNextLogin") var shouldChangePasswordOnNextLogin: Boolean? = null,
    @SerializedName("isTwoFactorEnabled") var isTwoFactorEnabled: Boolean? = null,
    @SerializedName("isLockoutEnabled") var isLockoutEnabled: Boolean? = null,
    @SerializedName("defaultLocationId") var defaultLocationId: Int? = null,
    @SerializedName("poLimit") var poLimit: Int? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("vendorId") var vendorId: String? = null,
    @SerializedName("salesPersonId") var salesPersonId: Int? = null

)