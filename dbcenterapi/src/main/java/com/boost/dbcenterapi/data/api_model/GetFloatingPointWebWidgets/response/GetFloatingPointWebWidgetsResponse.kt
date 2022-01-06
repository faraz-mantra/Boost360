package com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetFloatingPointWebWidgetsResponse(
  @SerializedName("Error")
  val `Error`: Error,
  @SerializedName("Result")
  val `Result`: List<String>,
  @SerializedName("StatusCode")
  val `StatusCode`: Int
): BaseResponse(), Serializable