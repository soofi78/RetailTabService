package com.lfsolutions.retail.model.profile

import com.google.gson.annotations.SerializedName


data class Roles (

  @SerializedName("roleId"          ) var roleId          : Int?     = null,
  @SerializedName("roleName"        ) var roleName        : String?  = null,
  @SerializedName("roleDisplayName" ) var roleDisplayName : String?  = null,
  @SerializedName("isAssigned"      ) var isAssigned      : Boolean? = null

)