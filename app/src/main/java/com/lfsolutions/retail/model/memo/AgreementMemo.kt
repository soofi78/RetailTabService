package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName

data class AgreementMemo(
    @SerializedName("Id") var Id: String? = null,
    @SerializedName("AgreementNo") var AgreementNo: String? = null,
    @SerializedName("AgreementDate") var AgreementDate: String? = null,
    @SerializedName("LocationId") var LocationId: Int? = null,
    @SerializedName("AgreementQty") var AgreementQty: Double? = null,
    @SerializedName("AgreementTotal") var AgreementTotal: Double? = null,
    @SerializedName("Remarks") var Remarks: String? = null,
    @SerializedName("TenantId") var TenantId: Int? = 0,
    @SerializedName("Status") var Status: String = "C",
    @SerializedName("CustomerId") var CustomerId: Int? = null,
    @SerializedName("IsDeleted") var IsDeleted: Boolean = false,
    @SerializedName("DeleterUserId") var DeleterUserId: String? = null,
    @SerializedName("DeletionTime") var DeletionTime: String? = null,
    @SerializedName("LastModificationTime") var LastModificationTime: String? = null,
    @SerializedName("LastModifierUserId") var LastModifierUserId: String? = null,
    @SerializedName("CreationTime") var CreationTime: String? = null,
    @SerializedName("CreatorUserId") var CreatorUserId: String? = null,
    @SerializedName("Signature") var Signature: String? = null
)