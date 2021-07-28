package com.dashboard.model.live.shareUser

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ShareUserDetailResponse(
  @SerializedName("data")
  var `data`: ArrayList<UserDetail>? = null
) : BaseResponse(), Serializable