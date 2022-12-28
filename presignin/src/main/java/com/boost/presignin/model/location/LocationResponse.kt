package com.boost.presignin.model.location

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class LocationResponse (
  @SerializedName("ip"  ) var ip  : String? = null,
  @SerializedName("geo" ) var geo : String? = null,
  @SerializedName("isp" ) var isp : String? = null
): BaseResponse() {
}