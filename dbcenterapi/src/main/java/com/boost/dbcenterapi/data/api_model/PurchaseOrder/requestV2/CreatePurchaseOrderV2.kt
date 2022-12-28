package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2

data class CreatePurchaseOrderV2(
  val ClientId: String,
  val FloatingPointId: String,
  val PaymentDetails: PaymentDetails,
  val PurchaseOrderType: String,
  val PurchaseOrders: List<PurchaseOrder>,
  val CartStateId: String
)