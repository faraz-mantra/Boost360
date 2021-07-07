package com.inventoryorder.model.orderRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShippingDetails(
  @SerializedName("CurrencyCode")
  var currencyCode: String? = null,
  @SerializedName("DeliveryMode")
  var deliveryMode: String? = null,
  @SerializedName("DeliveryProvider")
  var deliveryProvider: String? = null,
  @SerializedName("ShippedBy")
  var shippedBy: String? = null,
  @SerializedName("ShippingCost")
  var shippingCost: Double? = null,
) : Serializable {

  enum class ShippedBy {
    SELLER
  }

  enum class DeliveryProvider {
    NF_VIDEO_CONSULATION
  }
}