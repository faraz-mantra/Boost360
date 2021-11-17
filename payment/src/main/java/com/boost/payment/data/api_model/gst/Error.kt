package com.boost.payment.data.api_model.gst


import com.google.gson.annotations.SerializedName

data class Error(
  @SerializedName("ErrorCode")
  var errorCode: Any? = null,
  @SerializedName("ErrorList")
  var errorList: Any? = null
)