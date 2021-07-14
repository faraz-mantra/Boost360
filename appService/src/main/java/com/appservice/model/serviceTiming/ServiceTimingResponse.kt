package com.appservice.model.serviceTiming

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceTimingResponse(
  @SerializedName("StatusCode")
  var statusCode: Int? = null,
  @SerializedName("Error")
  var errorN: Any? = null,
  @SerializedName("Result")
  var result: ArrayList<ServiceTiming>? = null
) : BaseResponse(), Serializable {

}