package com.boost.presignin.model.userprofile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectUserProfileResult(
  @SerializedName("FpIds")
  val FpIds: Array<String>,
  @SerializedName("Channels")
  val Channels: AuthChannels,
) : Serializable