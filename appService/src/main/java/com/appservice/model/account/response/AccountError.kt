package com.appservice.model.account.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountError(
    @SerializedName("ErrorCode")
    var errorCode: Any? = null,
    @SerializedName("ErrorList")
    var errorList: AccountErrorList? = null
) : Serializable {

  fun getMessage(): String {
    return errorList?.message ?: errorList?.invalidParameter ?: "Error getting from server."
  }
}
