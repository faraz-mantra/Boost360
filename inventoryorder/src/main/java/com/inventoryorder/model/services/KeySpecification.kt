package com.inventoryorder.model.services


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KeySpecification(
  @SerializedName("key")
  var key: String? = null,
  @SerializedName("value")
  var value: String? = null
) : Serializable