package com.dashboard.model.live.premiumBanner


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PremiumBundle(
//  @SerializedName("createdon")
//  var createdon: String? = null,
  @SerializedName("desc")
  var desc: String? = null,
  @SerializedName("exclusive_for_customers")
  var exclusiveForCustomers: ArrayList<String>? = null,
  @SerializedName("exclusive_to_categories")
  var exclusiveToCategories: ArrayList<String>? = null,
  @SerializedName("included_features")
  var includedFeatures: ArrayList<IncludedFeature>? = null,
//  @SerializedName("isarchived")
//  var isarchived: Boolean? = null,
//  @SerializedName("_kid")
//  var kid: String? = null,
  @SerializedName("min_purchase_months")
  var minPurchaseMonths: Double? = null,
  @SerializedName("name")
  var name: String? = null,
  @SerializedName("overall_discount_percent")
  var overallDiscountPercent: Double? = null,
//  @SerializedName("_parentClassId")
//  var parentClassId: String? = null,
//  @SerializedName("_parentClassName")
//  var parentClassName: String? = null,
//  @SerializedName("primary_image")
//  var primaryImage: PrimaryImage? = null,
//  @SerializedName("_propertyName")
//  var propertyName: String? = null,
//  @SerializedName("target_business_usecase")
//  var targetBusinessUsecase: String? = null,
//  @SerializedName("updatedon")
//  var updatedon: String? = null,
//  @SerializedName("websiteid")
//  var websiteid: String? = null
) : Serializable