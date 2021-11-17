package com.boost.payment.data.api_model.RazorpayToken

data class Card(
  val emi: Boolean,
  val entity: String,
  val expiry_month: Int,
  val expiry_year: Int,
  val international: Boolean,
  val issuer: String,
  val last4: Int,
  val name: String,
  val network: String
)