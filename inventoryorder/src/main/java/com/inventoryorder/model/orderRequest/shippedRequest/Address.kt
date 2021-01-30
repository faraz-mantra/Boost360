package com.inventoryorder.model.orderRequest.shippedRequest


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address(
  @SerializedName("AddressLine1")
  var addressLine1: String = "",
  @SerializedName("AddressLine2")
  var addressLine2: String = "",
  @SerializedName("City")
  var city: String = "",
  @SerializedName("Country")
  var country: String = "",
  @SerializedName("Region")
  var region: String = "",
  @SerializedName("Zipcode")
  var zipcode: String = ""
): Serializable