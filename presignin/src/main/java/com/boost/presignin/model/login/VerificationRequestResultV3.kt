package com.boost.presignin.model.login

import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VerificationRequestResultV3(
  @SerializedName("StatusCode")
  var StatusCode: Int? = null,
  @SerializedName("Error")
  var errors: Error? = null,
  @SerializedName("Result")
  var result: VerificationRequestResult? = null,
) : BaseResponse(), Serializable