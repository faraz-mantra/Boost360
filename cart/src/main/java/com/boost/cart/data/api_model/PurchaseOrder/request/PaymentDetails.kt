package com.boost.cart.data.api_model.PurchaseOrder.request

data class PaymentDetails(
  val CurrencyCode: String,
  val Discount: Int,
  val PaymentChannelProvider: String,
  val TaxDetails: TaxDetails,
  val TotalPrice: Double
)