package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

data class PackageBundles(
  val bundle_id: String,
  val included_features: List<IncludedFeature>,
  val min_purchase_months: Int?,
  val name: String?,
  val overall_discount_percent: Int,
  val primary_image: PrimaryImage?,
  val target_business_usecase: String?,
  val exclusive_to_categories: List<String>?,
  val exclusive_for_customers: List<String>?
)