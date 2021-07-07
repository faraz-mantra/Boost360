package com.dashboard.model.live.websiteItem

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsiteDataResponse(
  @SerializedName("data")
  var `data`: ArrayList<WebsiteData>? = null
) : BaseResponse(), Serializable