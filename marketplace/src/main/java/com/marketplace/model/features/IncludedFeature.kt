package com.marketplace.model.features


import com.google.gson.annotations.SerializedName

data class IncludedFeature(
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("feature_code")
  var featureCode: String? = null,
  @SerializedName("feature_price_discount_percent")
  var featurePriceDiscountPercent: Double? = null,
  @SerializedName("feature_unit_count")
  var featureUnitCount: Double? = null,
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
)