package com.inventoryorder.model.ordersdetails

data class DeliveryConfirmationDetails(
    val ConfirmationToken: String? = null,
    val ConfirmedOn: String? = null,
    val DeliveryConfirmationStatus: String? = null,
    val DeliveryConfirmedBy: String? = null,
    val ExtraDeliveryInformation: ExtraDeliveryInformation? = null,
    val NotificationSentBy: String? = null,
    val NotificationSentOn: String? = null,
    val OrderIssue: String? = null,
    val OrderIssueReason: String? = null
)