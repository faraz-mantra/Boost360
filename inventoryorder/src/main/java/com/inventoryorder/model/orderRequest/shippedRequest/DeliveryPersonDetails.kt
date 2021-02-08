package com.inventoryorder.model.orderRequest.shippedRequest


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeliveryPersonDetails(
  @SerializedName("EmailId")
  var emailId: String = "",
  @SerializedName("FullName")
  var fullName: String = "",
  @SerializedName("PrimaryContactNumber")
  var primaryContactNumber: String = "",
  @SerializedName("SecondaryContactNumber")
  var secondaryContactNumber: String = ""
): Serializable