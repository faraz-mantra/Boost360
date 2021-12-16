package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2

data class PurchaseOrder(
  val CouponCode: String?,
  val Discount: Int,
  val ExtraPurchaseOrderDetails: ExtraPurchaseOrderDetails?,
  val NetPrice: Double,
  val Widgets: List<Widget>
)