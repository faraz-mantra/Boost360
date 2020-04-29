package com.inventoryorder.model.ordersdetails

data class ItemX(
    val ActualPrice: Double? = null,
    val Product: Product? = null,
    val Quantity: Int? = null,
    val SalePrice: Double? = null,
    val ShippingCost: Double? = null,
    val Type: String? = null
)