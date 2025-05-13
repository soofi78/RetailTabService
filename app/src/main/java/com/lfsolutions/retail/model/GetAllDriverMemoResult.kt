package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.memo.DriverMemo

data class GetAllDriverMemoResult(
    @SerializedName("totalCounts") var totalCounts: Int = 0,
    @SerializedName("items") var items: ArrayList<DriverMemo> = arrayListOf()
)