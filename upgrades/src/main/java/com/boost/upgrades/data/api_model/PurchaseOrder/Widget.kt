package com.boost.upgrades.data.api_model.PurchaseOrder

data class Widget(
    val Category: String,
    val ConsumptionType: String,
    val Desc: String,
    val Discount: Int,
    val ExpiryInDays: Int,
    val Images: List<String>,
    val Name: String,
    val Price: Int,
    val Quantity: Int,
    val WidgetKey: String
)