package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


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
    @SerializedName("paymentTerm") var paymentTerm: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("deleterUserId") var deleterUserId: String? = null,
    @SerializedName("deletionTime") var deletionTime: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("lastModifierUserId") var lastModifierUserId: String? = null,
    @SerializedName("creationTime") var creationTime: String? = null,
    @SerializedName("creatorUserId") var creatorUserId: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("selected") @Transient var isSelected: Boolean = false,
    @SerializedName("salesLimit") var salesLimit: Double? = null,
    @SerializedName("isTaxExempt") var isTaxExempt: Boolean? = null,
    @SerializedName("taxRegistrationNo") var taxRegistrationNo: String? = null,
    @SerializedName("balanceAmount") var balanceAmount: Double? = null,
    @SerializedName("priceGroupId") var priceGroupId: String? = null,
    @SerializedName("salespersonId") var salespersonId: String? = null,
    @SerializedName("salespersonName") var salespersonName: String? = null,
    @SerializedName("priceGroup") var priceGroup: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("isTaxInclusive") var isTaxInclusive: String? = null,
    @SerializedName("isWalkIn") var isWalkIn: String? = null,
    @SerializedName("isVisited") var IsVisited: Boolean? = null,
    @SerializedName("thirdPartyId") var thirdPartyId: String? = null,
    @SerializedName("bankDetails") var bankDetails: String? = null,
    @SerializedName("officialOpen") var officialOpen: String? = null,
    @SerializedName("operatingHours") var operatingHours: String? = null,
    @SerializedName("picName") var picName: String? = null,
    @SerializedName("customerWorkArea") var customerWorkArea: String? = null,
    @SerializedName("isVisitationSchedule") var isVisitationSchedule: Boolean? = null
) : Serializable

