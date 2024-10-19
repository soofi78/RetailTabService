package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CustomersResult(

    @SerializedName("items") var items: ArrayList<CustomersItem> = arrayListOf()

) {
    fun getUrgentCustomersList(): ArrayList<Customer> {
        val index = items.indexOf(CustomersItem("Urgent"))
        if (index > -1) return items[index].customers
        return arrayListOf()
    }

    fun getToVisitsCustomersList(): ArrayList<Customer> {
        val index = items.indexOf(CustomersItem("To Visits"))
        if (index > -1) return items[index].customers
        return arrayListOf()
    }

    fun getScheduledCustomersList(): ArrayList<Customer> {
        val index = items.indexOf(CustomersItem("Schedule"))
        if (index > -1) return items[index].customers
        return arrayListOf()
    }

    fun getScheduledVisitationCustomersList(): ArrayList<Customer> {
        val index = items.indexOf(CustomersItem("Visitation Schedule"))
        if (index > -1) return items[index].customers
        return arrayListOf()
    }

    fun getAll(): ArrayList<Customer> {
        val customers = ArrayList<Customer>()
        items.forEach {
            customers.addAll(it.customers)
        }
        return customers
    }
}