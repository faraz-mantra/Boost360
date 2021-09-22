package com.onboarding.nowfloats.model.googleAuth

import com.framework.base.BaseRequest

data class GoogleAuthTokenRequest(
  val client_id: String? = null,
  val client_secret: String? = null,
  val auth_code: String? = null,
  val grant_type: String? = "authorization_code",
  val redirect_uri: String? = ""
) : BaseRequest()