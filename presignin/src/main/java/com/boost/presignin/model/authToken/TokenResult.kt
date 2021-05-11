package com.boost.presignin.model.authToken

import com.framework.pref.UserSessionManager
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.util.*

data class TokenResult(
    @SerializedName("RefreshToken")
    var refreshToken: String? = null,
    @SerializedName("Token")
    var token: String? = null,
    var createDate: String = "",
) {

  fun getCreateDate(): Date? {
    return createDate.parseDate(DateUtils.FORMAT_SERVER_TO_LOCAL)
  }
}


fun UserSessionManager.getAccessTokenAuth(): TokenResult? {
  return convertStringToObj(this.getAccessToken ?: "")
}

fun UserSessionManager.saveAccessTokenAuth(tokenResult: TokenResult?) {
  tokenResult?.createDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_SERVER_TO_LOCAL) ?: ""
  this.storeAccessToken(convertObjToString(tokenResult) ?: "")
}