package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class RetailResponse<T>(@SerializedName("result") var result: T? = null)