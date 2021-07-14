package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class DeliveryConfirmationDetailsN(
  val ConfirmationToken: String? = null,
  val ConfirmedOn: String? = null,
  val DeliveryConfirmationStatus: String? = null,
  val DeliveryConfirmedBy: String? = null,
  val ExtraDeliveryInformation: ExtraDeliveryInformationN? = null,
  val NotificationSentBy: String? = null,
  val NotificationSentOn: String? = null,
  val OrderIssue: String? = null,
  val OrderIssueReason: String? = null
) : Serializable