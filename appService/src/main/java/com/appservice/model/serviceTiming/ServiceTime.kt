package com.appservice.model.serviceTiming


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceTime(
  @SerializedName("From")
  var from: String? = "09:30AM",
  @SerializedName("To")
  var to: String? = "07:30PM"
): Serializable