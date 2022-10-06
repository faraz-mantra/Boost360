package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2

data class TaxDetails(
  val GSTIN: String?,
  val TDS: Int,
  val TanNumber: String?,
  val Tax: Int
)