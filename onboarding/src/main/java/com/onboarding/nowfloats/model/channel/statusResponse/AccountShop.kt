package com.onboarding.nowfloats.model.channel.statusResponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountShop(
  @SerializedName("catalogId")
  var catalogId: String? = null,
  @SerializedName("merchantSettingsId")
  var merchantSettingsId: String? = null,
  @SerializedName("userAccountId")
  var userAccountId: String? = null,
) : Serializable