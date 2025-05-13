package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName


data class Vendor (

  @SerializedName("vendorCode"           ) var vendorCode           : String?  = null,
  @SerializedName("name"                 ) var name                 : String?  = null,
  @SerializedName("address1"             ) var address1             : String?  = null,
  @SerializedName("address2"             ) var address2             : String?  = null,
  @SerializedName("address3"             ) var address3             : String?  = null,
  @SerializedName("city"                 ) var city                 : String?  = null,
  @SerializedName("state"                ) var state                : String?  = null,
  @SerializedName("country"              ) var country              : String?  = null,
  @SerializedName("postalCode"           ) var postalCode           : String?  = null,
  @SerializedName("email"                ) var email                : String?  = null,
  @SerializedName("phoneNo"              ) var phoneNo              : String?  = null,
  @SerializedName("faxNo"                ) var faxNo                : String?  = null,
  @SerializedName("purchaseLimit"        ) var purchaseLimit        : Int?     = null,
  @SerializedName("thirdPartyId"         ) var thirdPartyId         : String?  = null,
  @SerializedName("isTaxExempt"          ) var isTaxExempt          : Boolean? = null,
  @SerializedName("taxRegistrationNo"    ) var taxRegistrationNo    : String?  = null,
  @SerializedName("balanceAmount"        ) var balanceAmount        : Int?     = null,
  @SerializedName("bankDetails"          ) var bankDetails          : String?  = null,
  @SerializedName("productCount"         ) var productCount         : Int?     = null,
  @SerializedName("isDeleted"            ) var isDeleted            : Boolean? = null,
  @SerializedName("deleterUserId"        ) var deleterUserId        : String?  = null,
  @SerializedName("deletionTime"         ) var deletionTime         : String?  = null,
  @SerializedName("lastModificationTime" ) var lastModificationTime : String?  = null,
  @SerializedName("lastModifierUserId"   ) var lastModifierUserId   : String?  = null,
  @SerializedName("creationTime"         ) var creationTime         : String?  = null,
  @SerializedName("creatorUserId"        ) var creatorUserId        : String?  = null,
  @SerializedName("id"                   ) var id                   : Int?     = null

)