package com.appservice.model.keyboard

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KeyboardTabsResponse(
  @SerializedName("data")
  var `data`: ArrayList<KeyboardData>? = null
) : Serializable, BaseResponse()