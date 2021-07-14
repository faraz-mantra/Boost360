package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class UserProfileVerificationRequest(
  @SerializedName("authProviderId")
  val authProviderId: String,
  @SerializedName("provider")
  val provider: String,
  @SerializedName("loginKey")
  val loginKey: String,
  @SerializedName("loginSecret")
  val loginSecret: String,
  @SerializedName("clientId")
  val clientId: String
)