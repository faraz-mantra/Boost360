package com.onboarding.nowfloats.model.channel.insights


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PageNegativeFeedbackByTypeUnique(
  @SerializedName("hide_all_clicks")
  var hideAllClicks: Int? = null,
  @SerializedName("hide_clicks")
  var hideClicks: Int? = null,
  @SerializedName("report_spam_clicks")
  var reportSpamClicks: Int? = null,
  @SerializedName("unlike_page_clicks")
  var unlikePageClicks: Int? = null,
) : Serializable