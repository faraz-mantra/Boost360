package com.appservice.model.serviceProduct.gstProduct


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActionDataG(
  @SerializedName("gst_slab")
  var gstSlab: Double? = null,
  @SerializedName("height")
  var height: Double? = null,
  @SerializedName("length")
  var length: Double? = null,
  @SerializedName("merchant_id")
  var merchantId: String? = null,
  @SerializedName("product_id")
  var productId: String? = null,
  @SerializedName("weight")
  var weight: Double? = null,
  @SerializedName("width")
  var width: Double? = null
) : Serializable