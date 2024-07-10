package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("tenantId") var tenantId: Int? = null,
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
    @SerializedName("customerId") var customerId: String? = null,
    @SerializedName("vendorId") var vendorId: String? = null,
    @SerializedName("result") var result: String? = null,
)