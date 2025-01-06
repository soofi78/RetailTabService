package com.lfsolutions.retail.model.product

import com.google.gson.annotations.SerializedName


data class AssetResult(

    @SerializedName("totalCount") var totalCount: Int? = null,
    @SerializedName("items") var items: ArrayList<Asset> = arrayListOf()

)