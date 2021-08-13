package com.marketplace.model.features


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FeatureResponse(
  @SerializedName("Data")
  var `data`: List<Data>? = null,
  @SerializedName("Extra")
  var extra: Extra? = null
): BaseResponse(),Serializable