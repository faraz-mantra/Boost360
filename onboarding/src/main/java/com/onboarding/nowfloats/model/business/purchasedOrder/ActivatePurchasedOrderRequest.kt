package com.onboarding.nowfloats.model.business.purchasedOrder


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActivatePurchasedOrderRequest(
  @SerializedName("ClientId")
  var clientId: String? = null,
  @SerializedName("FloatingPointId")
  var floatingPointId: String? = null,
  @SerializedName("PurchaseOrderType")
  var purchaseOrderType: String? = null,
  @SerializedName("Widgets")
  var widgets: ArrayList<PurchasedWidget>? = null
) : BaseRequest(), Serializable