package com.boost.presignup.datamodel.userprofile

import com.google.gson.annotations.SerializedName

data class ConnectUserProfileResponse(
  @SerializedName("Error")
  val Error: UserError,
  @SerializedName("Result")
  val Result: ConnectUserProfileResult,
  @SerializedName("StatusCode")
  val StatusCode: Int
)