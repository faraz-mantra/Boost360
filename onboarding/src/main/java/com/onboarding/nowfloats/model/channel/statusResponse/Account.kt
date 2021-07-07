package com.onboarding.nowfloats.model.channel.statusResponse

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Account(
  @SerializedName("accountId")
  var accountId: String? = null,
  @SerializedName("accountName")
  var accountName: String? = null,
) : Serializable