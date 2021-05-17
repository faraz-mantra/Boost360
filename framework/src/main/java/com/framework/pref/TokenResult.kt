package com.framework.pref

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.util.*

data class TokenResult(
    @SerializedName("RefreshToken")
    var refreshToken: String? = null, // expire in 30 days
    @SerializedName("Token")
    var token: String? = null, // expire in 1 days
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