package com.onboarding.nowfloats.model.googleAuth

import com.framework.base.BaseResponse

data class GoogleAuthResponse(
  val access_token: String? = null,
  val expires_in: Long? = null,
  val id_token: String? = null,
  val refresh_token: String? = null,
  val scope: String? = null,
  val token_type: String? = null
) : BaseResponse() {

  fun getAuth(): String {
    return "$token_type $access_token"
  }

  fun getExpiryDate(): String {
    return "$expires_in"
  }
}