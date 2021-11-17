package com.boost.payment.data.api_model.GetAllWidgets

data class Review(
  val businessType: String,
  val desc: String,
  val name: String,
  val profileImage: String,
  val rating: Int,
  val title: String
)