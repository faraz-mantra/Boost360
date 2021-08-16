package com.framework.pref

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.google.gson.annotations.SerializedName
import java.util.*
import java.util.concurrent.TimeUnit

data class TokenResult(
  @SerializedName("RefreshToken")
  var refreshToken: String? = null, // expire in 30 days
  @SerializedName("Token")
  var token: String? = null, // expire in 1 days
  var createDate: String = "",
) {

  fun isExpiredToken(): Boolean {
    val diffInMilliSec = getCurrentDate().time - (getCreateDate()?.time ?: 0L)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec)
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec)
    return diffInDays >= 1 || diffInHours >= 22
  }

  private fun getCreateDate(): Date? {
    return createDate.parseDate(DateUtils.FORMAT_SERVER_TO_LOCAL)
  }
}


fun UserSessionManager.getAccessTokenAuth(): TokenResult? {
  return convertStringToObj(this.getAccessToken ?: "")
}

fun getAccessTokenAuth1(s: UserSessionManager): TokenResult? {
  return s.getAccessTokenAuth()
}

fun UserSessionManager.saveAccessTokenAuth(tokenResult: TokenResult?) {
  tokenResult?.createDate = getCurrentDate().parseDate(DateUtils.FORMAT_SERVER_TO_LOCAL) ?: ""
  this.storeAccessToken(convertObjToString(tokenResult) ?: "")
}

fun saveAccessTokenAuth1(s: UserSessionManager, tokenResult: TokenResult?) {
  s.saveAccessTokenAuth(tokenResult)
}