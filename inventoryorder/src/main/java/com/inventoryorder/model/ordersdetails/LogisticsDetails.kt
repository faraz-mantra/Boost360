package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class LogisticsDetails(
    val Address: Address? = null,
    val DeliveredOn: Any? = null,
    val DeliveryConfirmationDetails: DeliveryConfirmationDetails? = null,
    val DeliveryMode: String? = null,
    val DeliveryProvider: Any? = null,
    val DeliveryProviderId: Any? = null,
    val LastShippingReminderSentOn: Any? = null,
    val ShippedBy: String? = null,
    val ShippedOn: Any? = null,
    val Status: String? = null,
    val TrackingNumber: Any? = null,
    val TrackingURL: Any? = null
) : Serializable