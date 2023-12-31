package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2

data class PaymentDetails(
  val CurrencyCode: String,
  val Discount: Double,
  val PaymentChannelProvider: String,
  val TaxDetails: TaxDetails,
  val TotalPrice: Double
)