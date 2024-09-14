package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class CustomerIdRequest(@SerializedName("customerId") var id: Int? = 0)