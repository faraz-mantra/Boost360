package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthChannels(
  @SerializedName("FACEBOOK") val fACEBOOK: Boolean,
  @SerializedName("GOOGLE") val gOOGLE: Boolean,
  @SerializedName("EMAIL") val eMAIL: Boolean,
  @SerializedName("OTP") val oTP: Boolean,
) : Serializable