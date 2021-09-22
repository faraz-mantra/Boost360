package com.appservice.model.serviceProduct.addProductImage


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageI(
  @SerializedName("description")
  var description: String? = null,
  @SerializedName("url")
  var url: String? = null
) : Serializable