package com.marketplace.model.features


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MarketplaceOffer(
  @SerializedName("coupon_code")
  var couponCode: String? = null,
  @SerializedName("createdon")
  var createdon: String? = null,
  @SerializedName("exclusive_to_categories")
  var exclusiveToCategories: List<Any>? = null,
  @SerializedName("expiry_date")
  var expiryDate: String? = null,
  @SerializedName("extra_information")
  var extraInformation: String? = null,
  @SerializedName("image")
  var image: Image? = null,
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
  @SerializedName("title")
  var title: String? = null,
  @SerializedName("updatedon")
  var updatedon: String? = null,
  @SerializedName("websiteid")
  var websiteid: String? = null
): Serializable