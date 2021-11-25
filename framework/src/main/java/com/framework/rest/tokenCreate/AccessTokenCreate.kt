package com.framework.rest.tokenCreate

import com.framework.base.BaseRequest
import com.framework.base.BaseResponse
import com.framework.pref.TokenResult
import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
  @SerializedName("Error")
  var error1: Any? = null,
  @SerializedName("Result")
  var result: TokenResult? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null,
) : BaseResponse(){

}

data class AccessTokenRequest(
  @SerializedName("authToken")
  var authToken: String? = null,
  @SerializedName("clientId")
  var clientId: String? = null,
  @SerializedName("fpId")
  var fpId: String? = null
) : BaseRequest()