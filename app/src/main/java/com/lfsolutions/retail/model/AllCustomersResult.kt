package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class AllCustomersResult(
    @SerializedName("items") var items: ArrayList<Customer> = arrayListOf()
)