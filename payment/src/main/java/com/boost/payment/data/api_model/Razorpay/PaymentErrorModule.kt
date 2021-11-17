package com.boost.payment.data.api_model.Razorpay

data class PaymentErrorModule(
  val error: Error,
  val http_status_code: Int
)