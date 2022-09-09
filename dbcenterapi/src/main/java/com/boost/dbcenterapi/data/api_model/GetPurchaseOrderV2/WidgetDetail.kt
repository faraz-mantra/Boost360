package com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2

data class WidgetDetail(
    val ActivationDate: String,
    val BaseWidgetId: Any,
    val Category: String,
    val ConsumptionConstraint: Any,
    val CreatedOn: String,
    val DependentWidget: Any,
    val Desc: String,
    val Discount: Double,
    val Expiry: Expiry,
    val FloatingPointId: String,
    val Images: Any,
    val InvoiceLink: String,
    val IsRecurringPayment: Boolean,
    val Name: String,
    val NetPrice: Double,
    val Price: Double,
    val Properties: Any,
    val Quantity: Int,
    val RecurringPaymentFrequency: Any,
    val WidgetKey: String
)