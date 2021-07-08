package com.boost.upgrades.data.api_model.GetAllWidgets

data class GetAllWidgets(
  val MRPPrice: Int,
  val RecurringPaymentFrequency: String,
  val discount: Int,
  val featureDetails: FeatureDetails,
  val id: String,
  val image: String,
  val name: String,
  val price: Int,
  val title: String
)