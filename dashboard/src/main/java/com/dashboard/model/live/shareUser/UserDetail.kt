package com.dashboard.model.live.shareUser

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDetail(
  @SerializedName("message")
  var message: String? = null,
  @SerializedName("type")
  var type: String? = null
):Serializable