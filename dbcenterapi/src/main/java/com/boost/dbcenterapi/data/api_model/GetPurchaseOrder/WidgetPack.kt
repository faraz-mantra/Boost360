package com.boost.dbcenterapi.data.api_model.GetPurchaseOrder

data class WidgetPack(
  val CreatedOn: String,
  val Desc: String,
  val Discount: Int,
  val ExpiryDate: String,
  val ExpiryInDays: Int,
  val ExpiryInMths: Int,
  val Name: String,
  val Price: Double,
  val Properties: Any,
  val WidgetKey: String
)