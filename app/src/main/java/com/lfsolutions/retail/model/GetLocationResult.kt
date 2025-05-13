package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class GetLocationResult(

    @SerializedName("items") var items: ArrayList<Locations> = arrayListOf()

)