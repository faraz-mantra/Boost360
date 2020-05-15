package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class ProductN(
    val CurrencyCode: String? = null,
    val Description: String? = null,
    val Dimensions: DimensionsN? = null,
    val DiscountAmount: Double? = null,
    val ExtraProperties: ExtraPropertiesN,
    val ImageUri: String? = null,
    val IsAvailable: Boolean? = null,
    val Name: String? = null,
    val Price: Double? = null,
    val SKU: String? = null,
    val ShippingCost: Double? = null,
    val _id: String? = null
) : Serializable