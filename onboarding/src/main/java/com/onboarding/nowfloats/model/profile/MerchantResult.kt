package com.onboarding.nowfloats.model.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MerchantResult(
  @SerializedName("ChannelProfileProperties")
  var channelProfileProperties: ArrayList<ChannelProfileProperty>? = null,
  @SerializedName("Channels")
  var channels: MerchantChannels? = null,
  @SerializedName("FpIds")
  var fpIds: ArrayList<String>? = null,
  @SerializedName("LoginId")
  var loginId: String? = null,
) : Serializable {
  fun getUserDetail(): ProfileProperties? {
    return channelProfileProperties?.firstOrNull()?.profileProperties
  }
}