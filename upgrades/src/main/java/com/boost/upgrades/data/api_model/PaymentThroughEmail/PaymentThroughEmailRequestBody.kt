package com.boost.upgrades.data.api_model.PaymentThroughEmail

data class PaymentThroughEmailRequestBody(
  val ClientId: String,
  val EmailBody: String,
  val From: String,
  val Subject: String,
  val To: List<String>,
  val Type: Int
)