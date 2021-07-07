package com.dashboard.model.live.premiumBanner

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpgradePremiumFeatureResponse(
  @SerializedName("Data")
  var `data`: ArrayList<PremiumFeatureData>? = null,
  @SerializedName("Extra")
  var extra: PremiumNExtra? = null
) : BaseResponse(), Serializable {

}