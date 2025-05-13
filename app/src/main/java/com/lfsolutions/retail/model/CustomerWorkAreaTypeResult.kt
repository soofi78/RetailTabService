package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CustomerWorkAreaTypeResult(
    @SerializedName("items") var items: ArrayList<WorkAreaTypes> = arrayListOf()
)