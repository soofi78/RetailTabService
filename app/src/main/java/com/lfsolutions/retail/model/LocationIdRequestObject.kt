package com.lfsolutions.retail.model

data class LocationIdRequestObject(val locationId: Int? = null)

data class ProductListRB(val locationId: Int? = null,val type:String?=null)

data class LocationIdCustomerIdRequestObject(
    val locationId: Int? = null,
    val customerId: Int? = null
)
