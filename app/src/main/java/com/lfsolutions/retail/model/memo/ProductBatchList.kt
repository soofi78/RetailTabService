package com.lfsolutions.retail.model.memo

import com.google.gson.annotations.SerializedName


data class ProductBatchList(
    @SerializedName("Id") var Id: Int? = null,
    @SerializedName("ProductId") var ProductId: Int? = null,
    @SerializedName("SerialNumber") var SerialNumber: String? = null,
    @SerializedName("BatchCode") var BatchCode: String? = null,
    @SerializedName("Qty") var Qty: Int = 1,
    @SerializedName("ExpiryDate") var ExpiryDate: String? = null,
    @SerializedName("UnitCost") var UnitCost: Int? = null,
    @SerializedName("Price") var Price: Int? = null,
    @SerializedName("MfgDate") var MfgDate: String? = null,
    @SerializedName("TenantId") var TenantId: Int? = null
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
