package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class FilterRequest(@SerializedName("filter") var filter: String? = null) {
    companion object {
        fun off(): FilterRequest = FilterRequest()
        fun on(): FilterRequest = FilterRequest("SR")
    }
}