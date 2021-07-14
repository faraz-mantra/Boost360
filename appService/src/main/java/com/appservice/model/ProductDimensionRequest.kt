package com.appservice.model

import com.google.gson.annotations.SerializedName

data class ProductDimensionRequest(

  @field:SerializedName("WebsiteId")
  var websiteId: String? = null,

  @field:SerializedName("ActionData")
  var actionData: ActionData? = null
)

data class ActionData(

  @field:SerializedName("product_id")
  var productId: String? = null,

  @field:SerializedName("length")
  var length: Double? = null,

  @field:SerializedName("width")
  var width: Double? = null,

  @field:SerializedName("weight")
  var weight: Double? = null,

  @field:SerializedName("merchant_id")
  var merchantId: String? = null,

  @field:SerializedName("height")
  var height: Double? = null
)
