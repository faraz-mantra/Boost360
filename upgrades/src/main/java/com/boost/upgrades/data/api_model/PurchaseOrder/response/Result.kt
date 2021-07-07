package com.boost.upgrades.data.api_model.PurchaseOrder.response

data class Result(
  val OrderId: String,
  val TotalPrice: Double,
  val TransactionId: String,
  val TransactionRequestLink: String
)