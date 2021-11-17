package com.boost.payment.data.api_model.GetAllFeatures.response

data class IncludedFeature(
  val feature_code: String,
  val feature_price_discount_percent: Int
)