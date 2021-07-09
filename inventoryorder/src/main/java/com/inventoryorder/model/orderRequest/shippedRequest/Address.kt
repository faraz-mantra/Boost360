package com.inventoryorder.model.orderRequest.shippedRequest


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address(
  @SerializedName("AddressLine1")
  var addressLine1: String ?=null,
  @SerializedName("AddressLine2")
  var addressLine2: String ?=null,
  @SerializedName("City")
  var city: String ?=null,
  @SerializedName("Country")
  var country: String ?=null,
  @SerializedName("Region")
  var region: String?=null,
  @SerializedName("Zipcode")
  var zipcode: String ?=null,
): Serializable