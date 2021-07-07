package com.boost.upgrades.data.api_model.PurchaseOrder.requestV2

data class ExtraPurchaseOrderDetails(
  val Description: String?,
  val Image: String?,
  val Name: String?,
  val Properties: List<Property>?
)