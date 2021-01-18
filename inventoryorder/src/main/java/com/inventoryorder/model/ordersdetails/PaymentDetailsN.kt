package com.inventoryorder.model.ordersdetails


import com.inventoryorder.utils.capitalizeUtil
import java.io.Serializable

data class PaymentDetailsN(
    val ExtraProperties: ExtraPropertiesN,
    val Method: String? = null,
    val OnlinePaymentProvider: String? = null,
    val Status: String? = null,
    val TransactionId: String? = null,
) : Serializable {

  fun status(): String {
    return (Status ?: "").capitalizeUtil()
  }

  fun method(): String {
    return Method ?: ""
  }

  fun methodValue(): String {
    return METHOD.fromType(method())?.value ?: ""
  }

  fun payment(): String {
    return if (methodValue().isNotEmpty() && status().isNotEmpty()) "${methodValue()}, ${status()}" else "${methodValue()}${status()}"
  }

  fun paymentWithColor(color: String?): String {
    return if (methodValue().isNotEmpty() && status().isNotEmpty()) "${methodValue()}, <font color='$color'>${status()}</font>" else "${methodValue()}<font color='$color'>${status()}</font>"
  }

  enum class METHOD(val value: String, val type: String) {
    COD("Offline", "COD"), ONLINEPAYMENT("Online", "ONLINEPAYMENT"), FREE("Free", "FREE"), ONLINE("Online", "ONLINE");

    companion object {
      fun fromType(type: String): METHOD? = values().firstOrNull { it.type.equals(type, ignoreCase = true) }
    }
  }

  enum class STATUS {
    PENDING, INITIATED, SUCCESS, INPROCESS, SUBMITTEDFORREFUND, REFUNDED, REFUNDDENIED, FAILED, CANCELLED;

    companion object {
      fun from(value: String): STATUS? = values().firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
  }
}