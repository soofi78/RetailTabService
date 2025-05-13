package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class AgreementMemoHistoryResult(
    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<AgreementMemo> = arrayListOf()

)