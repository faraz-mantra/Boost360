package com.boost.upgrades.data.api_model.PurchaseOrder.requestV2

data class PaymentDetails(
  val CurrencyCode: String,
  val Discount: Int,
  val PaymentChannelProvider: String,
  val TaxDetails: TaxDetails,
  val TotalPrice: Double
)