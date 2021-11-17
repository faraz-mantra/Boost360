package com.boost.payment.data.api_model.Razorpay

data class Error(
  val code: String,
  val description: String,
  val metadata: Any
)