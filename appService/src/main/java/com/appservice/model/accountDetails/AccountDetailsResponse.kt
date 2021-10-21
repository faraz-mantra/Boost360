package com.appservice.model.accountDetails

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountDetailsResponse(
  @SerializedName("Error")
  var errorN: ErrorDeatil? = null,
  @SerializedName("Result")
  var result: AccountResult? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
) : BaseResponse(), Serializable