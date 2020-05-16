package com.inventoryorder.model.ordersdetails

import java.io.Serializable
import java.util.*

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
    val TrackingURL: String? = null
) : Serializable {

  fun status(): String {
    return Status ?: ""
  }

  enum class STSTUS {
    NOT_INITIATED;

    companion object {
      fun from(value: String): STSTUS? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}