package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.service.ComplaintTypes
import com.lfsolutions.retail.ui.forms.FormType


data class Form(

    @SerializedName("title") var title: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("serialNo") var serialNo: String? = null,
    @SerializedName("lastModificationTime") var lastModificationTime: String? = null,
    @SerializedName("type") var type: String? = null

) {
    fun getType(): FormType? {
        return FormType.find(title.toString())
    }

    fun serialNumberAvailable(): Boolean {
        return serialNo == null || serialNo.equals("N/A", true).not()
    }
}