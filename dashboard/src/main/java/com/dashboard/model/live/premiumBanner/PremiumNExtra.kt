package com.dashboard.model.live.premiumBanner


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PremiumNExtra(
  @SerializedName("CurrentIndex")
  var currentIndex: Int? = null,
  @SerializedName("PageSize")
  var pageSize: Int? = null,
  @SerializedName("TotalCount")
  var totalCount: Int? = null
): Serializable