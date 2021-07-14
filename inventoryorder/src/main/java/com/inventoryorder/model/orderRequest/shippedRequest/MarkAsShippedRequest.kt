package com.inventoryorder.model.orderRequest.shippedRequest


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MarkAsShippedRequest(
  @SerializedName("Address")
  var address: Address? = null,
  @SerializedName("DeliveryCharges")
  var deliveryCharges: Double? = null,
  @SerializedName("DeliveryPersonDetails")
  var deliveryPersonDetails: DeliveryPersonDetails? = null,
  @SerializedName("DeliveryProvider")
  var deliveryProvider: String? = null,
  @SerializedName("OrderId")
  var orderId: String? = null,
  @SerializedName("ShippedBy")
  var shippedBy: String? = null,
  @SerializedName("ShippedOn")//2021-01-28T21:28:24.138Z
  var shippedOn: String? = null,
  @SerializedName("TrackingNumber")
  var trackingNumber: String? = null,
  @SerializedName("TrackingURL")
  var trackingURL: String? = null
) : BaseRequest(), Serializable {

  enum class ShippedBy(var value: String) {
    SELLER("SELLER"), NF("NF"), LOCAL_PERSON("Local Person")
  }
}