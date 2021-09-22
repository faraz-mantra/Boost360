package com.dashboard.model.live.quickAction


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuickActionResponse(
  @SerializedName("data")
  var `data`: ArrayList<ActionData>? = null,
) : BaseResponse(), Serializable