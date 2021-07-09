package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class DimensionsN(
    val Height: Double? = null,
    val Length: Double? = null,
    val Weight: Double? = null,
    val Width: Double? = null
) : Serializable