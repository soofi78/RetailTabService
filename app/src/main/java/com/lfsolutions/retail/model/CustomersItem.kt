package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CustomersItem(
    @SerializedName("name") var name: String? = null,
    @SerializedName("customers") var customers: ArrayList<Customer> = arrayListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (name.equals((other as CustomersItem).name)) {
            return true
        }
        return super.equals(other)
    }
}