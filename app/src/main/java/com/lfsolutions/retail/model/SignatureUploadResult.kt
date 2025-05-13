package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName


data class SignatureUploadResult(
    @SerializedName("fileName") var fileName: String? = null,
    @SerializedName("filePath") var filePath: String? = null,
    @SerializedName("status") var status: String? = null

)