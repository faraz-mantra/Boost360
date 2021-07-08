package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectUserProfileResponse(
  @SerializedName("Error")
  val Error: UserError,
  @SerializedName("Result")
  val Result: ConnectUserProfileResult,
  @SerializedName("StatusCode")
  val StatusCode: Int,
) : Serializable