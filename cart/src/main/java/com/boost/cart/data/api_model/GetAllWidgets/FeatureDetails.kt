package com.boost.cart.data.api_model.GetAllWidgets

data class FeatureDetails(
  val backgroundImage: String,
  val learnMore: LearnMore,
  val noOfbusinessUsed: Int,
  val review: List<Review>,
  val subDesc: String,
  val subImages: List<String>,
  val subTitle: String
)