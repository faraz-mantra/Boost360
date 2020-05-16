package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class InventoryDetailN(
    val Name: String? = null,
    val Value: Any? = null  /* it's value string or array */
) : Serializable