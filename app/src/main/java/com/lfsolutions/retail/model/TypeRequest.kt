package com.lfsolutions.retail.model

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.util.Constants.PRINT_TYPE_TAX_INVOICE

data class TypeRequest(@SerializedName("type") var type: Int? = PRINT_TYPE_TAX_INVOICE)