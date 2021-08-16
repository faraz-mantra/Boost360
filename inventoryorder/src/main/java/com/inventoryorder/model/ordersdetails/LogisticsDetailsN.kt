package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class LogisticsDetailsN(
  val Address: AddressN? = null,
  val DeliveredOn: String? = null,
  val DeliveryConfirmationDetails: DeliveryConfirmationDetailsN? = null,
  val DeliveryMode: String? = null,
  val DeliveryProvider: String? = null,
  val DeliveryProviderId: String? = null,
  val LastShippingReminderSentOn: String? = null,
  val ShippedBy: String? = null,
  val ShippedOn: String? = null,
  val Status: String? = null,
  val TrackingNumber: String? = null,
  val TrackingURL: String? = null,
) : Serializable {

  fun status(): String {
    return Status ?: ""
  }

  fun statusValue(): String {
    return LogisticsStatus.from(Status)?.title ?: status()
  }

  enum class LogisticsStatus(var title: String) {
    NOT_INITIATED("NOT-INITIATED");

    companion object {
      fun from(value: String?): LogisticsStatus? =
        values().firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
  }
}