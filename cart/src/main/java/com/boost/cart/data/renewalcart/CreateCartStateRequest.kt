package com.boost.cart.data.renewalcart


import com.google.gson.annotations.SerializedName

data class CreateCartStateRequest(
  @SerializedName("ClientId")
  var clientId: String? = null,
  @SerializedName("FloatingPointId")
  var floatingPointId: String? = null,
  @SerializedName("PurchaseOrderType")
  var purchaseOrderType: String? = null,
  @SerializedName("Widgets")
  var widgets: List<Widget>? = null
)