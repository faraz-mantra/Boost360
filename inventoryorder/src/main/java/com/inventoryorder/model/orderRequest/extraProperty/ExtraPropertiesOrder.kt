package com.inventoryorder.model.orderRequest.extraProperty

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExtraPropertiesOrder(
  @SerializedName("cancellationRemark")
  var cancellationRemark: String? = null,
  @SerializedName("deliveryRemark")
  var deliveryRemark: String? = null
) : Serializable