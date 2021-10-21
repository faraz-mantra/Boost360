package com.boost.upgrades.data.api_model.stateCode


import com.google.gson.annotations.SerializedName

data class Error(
  @SerializedName("ErrorCode")
  var errorCode: Any? = null,
  @SerializedName("ErrorList")
  var errorList: Any? = null
)