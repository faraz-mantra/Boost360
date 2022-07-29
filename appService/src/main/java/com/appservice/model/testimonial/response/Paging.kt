package com.appservice.model.testimonial.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Paging(
  @SerializedName("TotalCount")
  var totalCount: Int? = null,
  @SerializedName("Count")
  var count: Int? = null,
  @SerializedName("Limit")
  var limit: Int? = null,
  @SerializedName("Skip")
  var skip: Int? = null
) : Serializable