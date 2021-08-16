package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable

data class TokenResponse(
  val access_token: String? = null,
  val expires_in: Long? = null,
  val refresh_token: String? = null,
  val token_type: String? = null
) : Serializable