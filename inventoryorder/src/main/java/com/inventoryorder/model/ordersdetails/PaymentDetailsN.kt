package com.inventoryorder.model.ordersdetails

import java.io.Serializable
import java.util.*

data class PaymentDetailsN(
    val ExtraProperties: ExtraPropertiesN,
    val Method: String? = null,
    val OnlinePaymentProvider: String? = null,
    val Status: String? = null,
    val TransactionId: String? = null
) : Serializable {

  fun status(): String {
    return Status ?: ""
  }

  fun method(): String {
    return Method ?: ""
  }

  fun payment(): String {
    return if (method().isNotEmpty() && status().isNotEmpty()) "${method()}, ${status()}" else method()
  }

  enum class METHOD {
    COD, ONLINEPAYMENT;

    companion object {
      fun from(value: String): METHOD? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  enum class STATUS {
    PENDING, INITIATED, SUCCESS, INPROCESS, SUBMITTEDFORREFUND, REFUNDED, REFUNDDENIED, FAILED, CANCELLED;

    companion object {
      fun from(value: String): STATUS? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}