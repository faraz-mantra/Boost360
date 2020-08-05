package com.appservice.model.account.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Error(
    @SerializedName("ErrorCode")
    var errorCode: Any? = null,
    @SerializedName("ErrorList")
    var errorList: ErrorList? = null
) : Serializable {

  fun getMessage(): String {
    return errorList?.message ?: errorList?.invalidParameter ?: "Error getting from server."
  }
}
