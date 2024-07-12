package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class FormResult(@SerializedName("items") var items: ArrayList<Form> = arrayListOf())