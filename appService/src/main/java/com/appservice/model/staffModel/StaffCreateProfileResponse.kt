package com.appservice.model.staffModel

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffCreateProfileResponse(
  @SerializedName("Error")
  val errorCode: Any? = null,
  @SerializedName("StatusCode")
  val statusCode: Int? = null,
  @SerializedName("Result")
  val result: String? = null,
) : BaseResponse(), Serializable
