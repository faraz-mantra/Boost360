package com.boost.dbcenterapi.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class RegisteredBusinessAddress(
  @SerializedName("City")
  var city: Any? = null,
  @SerializedName("Country")
  var country: Any? = null,
  @SerializedName("Line1")
  var line1: Any? = null,
  @SerializedName("Line2")
  var line2: Any? = null,
  @SerializedName("State")
  var state: Any? = null
)