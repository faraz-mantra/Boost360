package com.appservice.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OtherSpecification(
  @SerializedName("key")
  var key: String? = null,
  @SerializedName("value")
  var value: String? = null
) : Serializable