package com.boost.upgrades.data.api_model.PurchaseOrder.request

data class Widget(
    val ConsumptionConstraint: ConsumptionConstraint,
    val Desc: String?,
    val Discount: Int,
    val Expiry: Expiry,
    val Images: List<String>,
    val IsCancellable: Boolean,
    val IsRecurringPayment: Boolean,
    val Name: String,
    val Price: Double,
    val RecurringPaymentFrequency: String,
    val WidgetKey: String,
    val Quantity: Int,
    val Properties: List<ExtendedProperties>?
)