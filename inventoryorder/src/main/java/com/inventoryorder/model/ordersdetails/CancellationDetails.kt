package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class  CancellationDetails(
    val CancelledBy: String? = null,
    val CancelledOn: String? = null
) : Serializable