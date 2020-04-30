package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class PaymentDetails(
    val Method: String? = null,
    val OnlinePaymentProvider: String? = null,
    val Status: String? = null,
    val TransactionId: String? = null
) : Serializable