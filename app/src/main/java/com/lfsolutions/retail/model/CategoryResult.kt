package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CategoryResult(
    @SerializedName("items") var items: ArrayList<CategoryItem> = arrayListOf()
)