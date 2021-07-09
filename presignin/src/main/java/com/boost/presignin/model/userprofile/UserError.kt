package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserError(
  @SerializedName("ErrorCode")
  val ErrorCode: Any? = null,
  @SerializedName("ErrorList")
  val ErrorList: ErrorList? = null
):Serializable

data class ErrorList(
  @SerializedName("EXCEPTION")
  var eXCEPTION: String? = null
):Serializable