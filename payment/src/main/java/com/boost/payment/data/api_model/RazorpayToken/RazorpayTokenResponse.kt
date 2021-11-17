package com.boost.payment.data.api_model.RazorpayToken

data class RazorpayTokenResponse(
  val count: Int,
  val entity: String,
  val items: List<Item>
)