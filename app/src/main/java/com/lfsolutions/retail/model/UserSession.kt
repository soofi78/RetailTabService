package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class UserSession(
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
    @SerializedName("salesPersonId") var salesPersonId: Int? = null,
    @SerializedName("timeZoneUtc") var timeZoneUtc: String? = null,
    @SerializedName("currencySymbol") var currencySymbol: String? = null,
    @SerializedName("currencyCode") var currencyCode: String? = null,
    @SerializedName("countryCode") var countryCode: String? = null,
    @SerializedName("salesTaxInclusive") var salesTaxInclusive: Boolean? = null,
    @SerializedName("member") var member: Boolean? = null,
    @SerializedName("retailTabPurchase") var retailTabPurchase: Boolean? = null,
    @SerializedName("customer") var customer: Boolean? = null,
    @SerializedName("employee") var employee: Boolean? = null,
    @SerializedName("dashboard") var dashboard: Boolean? = null,
    @SerializedName("posInvoiceRounded") var posInvoiceRounded: Int? = null,
    @SerializedName("defaultLocationId") var defaultLocationId: Int? = null,
    @SerializedName("defaultLocation") var defaultLocation: String? = null,
    @SerializedName("locationCode") var locationCode: String? = null,
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("vendorId") var vendorId: String? = null,
    @SerializedName("result") var result: String? = null,
    @SerializedName("userName") var userName: String? = "",
    @SerializedName("isSuperVisor") var isSupervisor: Boolean? = true,
    @SerializedName("wareHouseLocationId") var wareHouseLocationId: Int? = null,
    @SerializedName("wareHouseLocationName") var wareHouseLocationName: String? = null,
    @SerializedName("isEditPrice") var isEditPrice: Boolean = false
)