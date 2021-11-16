package com.boost.cart.data.api_model.customerId.create

data class TaxDetails(
  val TanNumber: String?,
  val Tax: Int,
  val GSTIN: String?,
  val TDS: Int
)