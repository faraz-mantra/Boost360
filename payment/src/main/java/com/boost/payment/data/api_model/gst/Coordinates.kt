package com.boost.payment.data.api_model.gst


import com.google.gson.annotations.SerializedName

data class Coordinates(
  @SerializedName("Latitude")
  var latitude: Any? = null,
  @SerializedName("Longitude")
  var longitude: Any? = null
)