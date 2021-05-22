package com.onboarding.nowfloats.model.plan


import com.google.gson.annotations.SerializedName

data class ExtraNProperty(
  @SerializedName("key")
  var key: String? = null,
  @SerializedName("value")
  var value: Int? = null,
  @SerializedName("widget")
  var widget: String? = null
)