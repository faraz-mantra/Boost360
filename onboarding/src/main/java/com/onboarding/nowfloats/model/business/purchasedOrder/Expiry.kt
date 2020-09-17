package com.onboarding.nowfloats.model.business.purchasedOrder


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Expiry(
  @SerializedName("Key")
  var key: String? = null,
  @SerializedName("Value")
  var value: Int? = null
): Serializable