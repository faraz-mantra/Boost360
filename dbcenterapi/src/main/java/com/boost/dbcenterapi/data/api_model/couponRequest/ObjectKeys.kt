package com.boost.dbcenterapi.data.api_model.couponRequest


import com.google.gson.annotations.SerializedName

data class ObjectKeys(
  @SerializedName("code")
  var code: Boolean? = null,
  @SerializedName("description")
  var description: Boolean? = null,
  @SerializedName("discount_percent")
  var discountPercent: Boolean? = null,
  @SerializedName("_kid")
  var kid: Boolean? = null,
  @SerializedName("termsandconditions")
  var termsandconditions: Boolean? = null,
  @SerializedName("title")
  var title: Boolean? = null
)