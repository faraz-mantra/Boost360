package com.dashboard.model.live.premiumBanner


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DiscountCoupon(
  @SerializedName("code")
  var code: String? = null,
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("discount_percent")
  var discountPercent: Double? = null,
  @SerializedName("isarchived")
  var isarchived: Boolean? = null,
  @SerializedName("_kid")
  var kid: String? = null,
  @SerializedName("_parentClassId")
  var parentClassId: String? = null,
  @SerializedName("_parentClassName")
  var parentClassName: String? = null,
  @SerializedName("_propertyName")
  var propertyName: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
) : Serializable