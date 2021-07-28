package com.dashboard.model.live.dashboardBanner


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BannerExtra(
  @SerializedName("CurrentIndex")
  var currentIndex: Int? = null,
  @SerializedName("PageSize")
  var pageSize: Int? = null,
  @SerializedName("TotalCount")
  var totalCount: Int? = null
) : Serializable