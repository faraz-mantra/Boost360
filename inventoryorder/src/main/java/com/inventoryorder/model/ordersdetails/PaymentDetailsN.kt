package com.inventoryorder.model.ordersdetails

import java.io.Serializable
import java.util.*

data class PaymentDetailsN(
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

  enum class METHOD {
    COD, ONLINEPAYMENT;

    companion object {
      fun from(value: String): METHOD? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  enum class STSTUS {
    SUCCESS;

    companion object {
      fun from(value: String): STSTUS? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}