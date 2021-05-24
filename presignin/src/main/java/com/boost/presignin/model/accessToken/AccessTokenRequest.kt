package com.boost.presignin.model.accessToken


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class AccessTokenRequest(
  @SerializedName("authToken")
  var authToken: String? = null,
  @SerializedName("clientId")
  var clientId: String? = null,
  @SerializedName("fpId")
  var fpId: String? = null
):BaseRequest()