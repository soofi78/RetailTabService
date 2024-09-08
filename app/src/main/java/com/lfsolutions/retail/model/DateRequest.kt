package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName

data class DateRequest(@SerializedName("date") var date: String?)
data class VisitDateRequest(@SerializedName("visitDate") var VisitDate: String?)