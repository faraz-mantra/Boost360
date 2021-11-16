package com.boost.cart.data.api_model.GetPurchaseOrder

data class GetPurchaseOrderResponse(
  val Error: Error,
  val Result: List<Result>,
  val StatusCode: Int
)