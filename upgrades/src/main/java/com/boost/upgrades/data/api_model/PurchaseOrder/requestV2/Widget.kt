package com.boost.upgrades.data.api_model.PurchaseOrder.requestV2

data class Widget(
  val Category: String,
  val ConsumptionConstraint: ConsumptionConstraint,
  val DependentWidget: String?,
  val Desc: String?,
  val Discount: Int,
  val Expiry: Expiry,
  val Images: List<String>,
  val IsCancellable: Boolean,
  val IsRecurringPayment: Boolean,
  val Name: String,
  val NetPrice: Double,
  val Price: Double,
  val Properties: List<Property>?,
  val Quantity: Int,
  val RecurringPaymentFrequency: String,
  val WidgetKey: String,
  val BaseWidgetId: String
)