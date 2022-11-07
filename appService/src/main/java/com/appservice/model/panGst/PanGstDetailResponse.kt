package com.appservice.model.panGst

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PanGstDetailResponse(
  @SerializedName("Error")
  var errorN: Any? = null,
  @SerializedName("Result")
  var result: PanGstResult? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
) : BaseResponse(), Serializable