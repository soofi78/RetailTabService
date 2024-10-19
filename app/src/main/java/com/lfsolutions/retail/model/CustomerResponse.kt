package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class CustomerResponse(@SerializedName("customer") var customer: Customer? = null)