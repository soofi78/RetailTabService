package com.lfsolutions.retail.model.sale.order

import com.google.gson.annotations.SerializedName
import com.lfsolutions.retail.model.sale.TaxForProduct
import com.lfsolutions.retail.model.ApplicableTaxes


data class SalesOrderDetail (

  @SerializedName("id"               ) var id               : String?                    = null,
  @SerializedName("slNo"             ) var slNo             : Int?                       = null,
  @SerializedName("productId"        ) var productId        : Int?                       = null,
  @SerializedName("inventoryCode"    ) var inventoryCode    : String?                    = null,
  @SerializedName("qty"              ) var qty              : String?                    = null,
  @SerializedName("price"            ) var price            : String?                    = null,
  @SerializedName("itemDiscount"     ) var itemDiscount     : Int?                       = null,
  @SerializedName("itemDiscountPerc" ) var itemDiscountPerc : Int?                       = null,
  @SerializedName("subTotal"         ) var subTotal         : Int?                       = null,
  @SerializedName("totalValue"       ) var totalValue       : Int?                       = null,
  @SerializedName("netDiscount"      ) var netDiscount      : Int?                       = null,
  @SerializedName("tax"              ) var tax              : Double?                    = null,
  @SerializedName("netTotal"         ) var netTotal         : Double?                    = null,
  @SerializedName("taxForProduct"    ) var taxForProduct    : ArrayList<TaxForProduct>   = arrayListOf(),
  @SerializedName("applicableTaxes"  ) var applicableTaxes  : ArrayList<ApplicableTaxes> = arrayListOf(),
  @SerializedName("uom"              ) var uom              : String?                    = null,
  @SerializedName("unitId"           ) var unitId           : Int?                       = null,
  @SerializedName("changeunitflag"   ) var changeunitflag   : Boolean?                   = null,
  @SerializedName("priceGroupId"     ) var priceGroupId     : String?                    = null

)