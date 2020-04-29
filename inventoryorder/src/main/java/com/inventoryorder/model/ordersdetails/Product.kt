package com.inventoryorder.model.ordersdetails

data class Product(
    val CurrencyCode: String? = null,
    val Description: Any? = null,
    val Dimensions: Any? = null,
    val DiscountAmount: Double? = null,
    val ExtraProperties: ExtraProperties,
    val ImageUri: String? = null,
    val IsAvailable: Boolean? = null,
    val Name: String? = null,
    val Price: Double? = null,
    val SKU: Any? = null,
    val ShippingCost: Double? = null,
    val _id: String? = null
)