package com.boost.upgrades.data.renewalcart


import com.google.gson.annotations.SerializedName

data class Error(
  @SerializedName("ErrorCode")
  var errorCode: String? = null,
  @SerializedName("ErrorList")
  var errorList: ErrorList? = null
)