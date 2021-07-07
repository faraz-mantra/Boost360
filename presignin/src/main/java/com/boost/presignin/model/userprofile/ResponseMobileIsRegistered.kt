package com.boost.presignin.model.userprofile

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseMobileIsRegistered(


  @field:SerializedName("StatusCode")
  val statusCode: Int? = null,

  @field:SerializedName("Result")
  val result: Boolean? = null,
) : BaseResponse(), Serializable

data class Error(

  @field:SerializedName("ErrorList")
  val errorList: Any? = null,

  @field:SerializedName("ErrorCode")
  val errorCode: Any? = null,
)
