package com.dashboard.model.live.dashboardBanner


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DashboardPremiumBannerResponse(
    @SerializedName("Data")
  var `data`: ArrayList<DashboardBannerData>? = null,
    @SerializedName("Extra")
  var extra: BannerExtra? = null
):BaseResponse(),Serializable