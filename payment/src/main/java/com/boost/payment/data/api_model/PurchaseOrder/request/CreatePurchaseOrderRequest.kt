package com.boost.payment.data.api_model.PurchaseOrder.request

data class CreatePurchaseOrderRequest(
  val ClientId: String,
  val FloatingPointId: String,
  val PaymentDetails: PaymentDetails,
  val Widgets: List<Widget>,
  val CouponCode: String?
)