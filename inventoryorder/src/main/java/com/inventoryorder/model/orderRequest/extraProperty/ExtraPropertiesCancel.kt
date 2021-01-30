package com.inventoryorder.model.orderRequest.extraProperty

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExtraPropertiesCancel(
  @SerializedName("cancellationRemark")
  var cancellationRemark: String = ""
): Serializable