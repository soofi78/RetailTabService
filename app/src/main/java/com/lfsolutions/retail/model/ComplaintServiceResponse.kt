package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

class ComplaintServiceResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("transactionNo") var transactionNo: String? = null
)