package com.appservice.model.serviceTiming


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceTime(
  @SerializedName("From")
  var from: String? = "",
  @SerializedName("To")
  var to: String? = ""
) : Serializable