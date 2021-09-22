package com.boost.presignin.model.login

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserProfileVerificationRequest(
  @SerializedName("authProviderId")
  val authProviderId: String? = "",
  @SerializedName("provider")
  val provider: String? = "",
  @SerializedName("loginKey")
  val loginKey: String?,
  @SerializedName("loginSecret")
  val loginSecret: String?,
  @SerializedName("clientId")
  val clientId: String?,
) : BaseRequest(), Serializable