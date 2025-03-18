package com.lfsolutions.retail.model

data class LocationIdRequestObject(val locationId: Int? = null)

data class LocationTenantIdRequestObject(
    val locationId: Int? = null,
    val tenantId: Int? = null,
    val customerWorkAreaId: Int = 0
)

data class ProductListRB(
    val locationId: Int? = null,
    val type: String? = null,
    val isSaleInvoice: Boolean = false
)

data class LocationIdCustomerIdRequestObject(
    val locationId: Int? = null,
    val customerId: Int? = null
)
