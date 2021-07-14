package com.appservice.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UniquePaymentUrl(
  @SerializedName("description")
  var description: String? = null,
  @SerializedName("url")
  var url: String? = null
) : Serializable