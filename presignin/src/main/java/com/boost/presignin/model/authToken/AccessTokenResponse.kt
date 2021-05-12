package com.boost.presignin.model.authToken

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
) : BaseResponse()