package com.onboarding.nowfloats.model.channel.insights


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InsightsData(
  @SerializedName("page_call_phone_clicks_logged_in_unique")
  var pageCallPhoneClicksLoggedInUnique: Int? = null,
  @SerializedName("page_consumptions_unique")
  var pageConsumptionsUnique: Int? = null,
  @SerializedName("page_engaged_users")
  var pageEngagedUsers: Int? = null,
  @SerializedName("page_follows")
  var pageFollows: Int? = null,
  @SerializedName("page_get_directions_clicks_logged_in_unique")
  var pageGetDirectionsClicksLoggedInUnique: Int? = null,
  @SerializedName("page_impressions_unique")
  var pageImpressionsUnique: Int? = null,
  @SerializedName("page_likes")
  var pageLikes: Int? = null,
  @SerializedName("page_negative_feedback_by_type_unique")
  var pageNegativeFeedbackByTypeUnique: PageNegativeFeedbackByTypeUnique? = null,
  @SerializedName("page_negative_feedback_unique")
  var pageNegativeFeedbackUnique: Int? = null,
  @SerializedName("page_places_checkin_total_unique")
  var pagePlacesCheckinTotalUnique: Int? = null,
  @SerializedName("page_positive_feedback_by_type_unique")
  var pagePositiveFeedbackByTypeUnique: PagePositiveFeedbackByTypeUnique? = null,
  @SerializedName("page_post_engagements")
  var pagePostEngagements: Int? = null,
  @SerializedName("page_posts_impressions_unique")
  var pagePostsImpressionsUnique: Int? = null,
  @SerializedName("page_total_actions")
  var pageTotalActions: Int? = null,
  @SerializedName("page_views_total")
  var pageViewsTotal: Int? = null,
  @SerializedName("page_website_clicks_logged_in_unique")
  var pageWebsiteClicksLoggedInUnique: Int? = null,
) : Serializable