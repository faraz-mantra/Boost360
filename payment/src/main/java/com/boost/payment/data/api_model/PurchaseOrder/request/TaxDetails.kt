package com.boost.payment.data.api_model.PurchaseOrder.request

data class TaxDetails(
  val GSTIN: String?,
  val TDS: Int,
  val TanNumber: String?,
  val Tax: Int
)