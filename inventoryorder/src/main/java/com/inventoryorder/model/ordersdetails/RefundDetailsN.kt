package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class RefundDetailsN(
    val Amount: Int,
    val CurrencyCode: String,
    val ExtraProperties: ExtraPropertiesN,
    val RefundMode: String,
    val RefundedBy: String,
    val RefundedOn: String
) : Serializable