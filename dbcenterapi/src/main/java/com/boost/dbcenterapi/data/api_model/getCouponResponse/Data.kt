package com.boost.dbcenterapi.data.api_model.getCouponResponse


import com.google.gson.annotations.SerializedName

data class Data(
  @SerializedName("code")
  var code: String? = null,
  @SerializedName("description")
  var description: String? = null,
  @SerializedName("discount_percent")
  var discountPercent: Double? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("termsandconditions")
  var termsandconditions: String? = null,
  @SerializedName("title")
  var title: String? = null
)