package com.boost.cart.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class ExtraProperties(
  @SerializedName("vpa")
  var vpa: String? = null
)