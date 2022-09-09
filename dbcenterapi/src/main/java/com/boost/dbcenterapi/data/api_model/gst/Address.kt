package com.boost.dbcenterapi.data.api_model.gst


import com.google.gson.annotations.SerializedName

data class Address(
  @SerializedName("AddressLine1")
  var addressLine1: String? = null,
  @SerializedName("AddressLine2")
  var addressLine2: String? = null,
  @SerializedName("City")
  var city: String? = null,
  @SerializedName("Coordinates")
  var coordinates: Coordinates? = null,
  @SerializedName("District")
  var district: String? = null,
  @SerializedName("Pincode")
  var pincode: String? = null,
  @SerializedName("State")
  var state: String? = null
)