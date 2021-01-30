package com.inventoryorder.model.orderRequest.shippedRequest


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MarkAsShippedRequest(
  @SerializedName("Address")
  var address: Address = Address(),
  @SerializedName("DeliveryCharges")
  var deliveryCharges: Double = 0.0,
  @SerializedName("DeliveryPersonDetails")
  var deliveryPersonDetails: DeliveryPersonDetails = DeliveryPersonDetails(),
  @SerializedName("DeliveryProvider")
  var deliveryProvider: String = "",
  @SerializedName("OrderId")
  var orderId: String = "",
  @SerializedName("ShippedBy")
  var shippedBy: String = "",
  @SerializedName("ShippedOn")//2021-01-28T21:28:24.138Z
  var shippedOn: String = "",
  @SerializedName("TrackingNumber")
  var trackingNumber: String = "",
  @SerializedName("TrackingURL")
  var trackingURL: String = ""
):BaseRequest(),Serializable{

  enum class ShippedBy{
    SELLER, NF
  }
}