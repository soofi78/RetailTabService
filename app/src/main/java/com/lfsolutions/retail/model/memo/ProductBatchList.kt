package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class ProductBatchList(
    @SerializedName("Id", alternate = arrayOf("id")) var Id: Int? = null,
    @SerializedName("ProductId", alternate = arrayOf("productId")) var ProductId: Int? = null,
    @SerializedName("SerialNumber", alternate = arrayOf("serialNumber")) var SerialNumber: String? = null,
    @SerializedName("BatchCode", alternate = arrayOf("batchCode")) var BatchCode: String? = null,
    @SerializedName("Qty", alternate = arrayOf("qty")) var Qty: Int = 1,
    @SerializedName("ExpiryDate", alternate = arrayOf("expiryDate")) var ExpiryDate: String? = null,
    @SerializedName("UnitCost", alternate = arrayOf("unitCost")) var UnitCost: Int? = null,
    @SerializedName("Price", alternate = arrayOf("price")) var Price: Int? = null,
    @SerializedName("MfgDate", alternate = arrayOf("mfgDate")) var MfgDate: String? = null,
    @SerializedName("TenantId", alternate = arrayOf("tenantId")) var TenantId: Int? = null
)


//    "BatchCode": null,
//    "ExpiryDate": null,
//    "Id": 1555,
//    "MfgDate": null,
//    "Price": null,
//    "ProductId": null,
//    "Qty": 1,
//    "SerialNumber": "A-1234566",
//    "TenantId": null,
//    "UnitCost": null
