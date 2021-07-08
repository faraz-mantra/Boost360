package com.onboarding.nowfloats.model.channel.insights


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PagePositiveFeedbackByTypeUnique(
  @SerializedName("answer")
  var answer: Int? = null,
  @SerializedName("claim")
  var claim: Int? = null,
  @SerializedName("comment")
  var comment: Int? = null,
  @SerializedName("like")
  var like: Int? = null,
  @SerializedName("link")
  var link: Int? = null,
  @SerializedName("other")
  var other: Int? = null,
  @SerializedName("rsvp")
  var rsvp: Int? = null,
) : Serializable