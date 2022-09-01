package com.boost.dbcenterapi.data.api_model.PurchaseOrder.response

data class CreatePurchaseOrderResponse(
  val Error: Error,
  val Result: Result,
  val StatusCode: Int
)