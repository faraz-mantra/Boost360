package com.boost.payment.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class Error(
  @SerializedName("ErrorCode")
  var errorCode: Any? = null,
  @SerializedName("ErrorList")
  var errorList: ErrorList? = null
)