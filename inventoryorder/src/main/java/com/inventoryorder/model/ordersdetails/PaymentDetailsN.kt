package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class PaymentDetailsN(
    val ExtraProperties: Any,
    val Method: String? = null,
    val OnlinePaymentProvider: String? = null,
    val Status: String? = null,
    val TransactionId: String? = null,
    val TransactionReferenceNo: String? = null,
) : Serializable {

  fun status(): String {
    return Status ?: ""
  }

  fun statusValue(): String {
    return STATUS.from(Status)?.title ?: status()
  }

  fun method(): String {
    return Method ?: ""
  }

  fun methodValue(): String {
    return METHOD.fromType(method())?.value ?: ""
  }

  fun payment(): String {
    return if (methodValue().isNotEmpty() && statusValue().isNotEmpty()) "${methodValue()}, ${statusValue()}" else "${methodValue()}${statusValue()}"
  }

  fun paymentWithColor(color: String?): String {
    return if (methodValue().isNotEmpty() && statusValue().isNotEmpty()) "${methodValue()}, <font color='$color'>${statusValue()}</font>" else "${methodValue()}<font color='$color'>${statusValue()}</font>"
  }

  enum class METHOD(val value: String, val type: String) {
    COD("COD", "COD"), ONLINEPAYMENT("ONLINE", "ONLINEPAYMENT"), FREE("FREE", "FREE"), ONLINE("ONLINE", "ONLINE");

    companion object {
      fun fromType(type: String?): METHOD? = values().firstOrNull { it.type.equals(type, ignoreCase = true) }
    }
  }

  enum class STATUS(var title: String) {
    PENDING("PENDING"), INITIATED("INITIATED"), SUCCESS("RECEIVED"), INPROCESS("IN-PROCESS"),
    SUBMITTEDFORREFUND("SUBMITFORREFUND"), REFUNDED("REFUNDED"), REFUNDDENIED("REFUND-DENIED"), FAILED("FAILED"), CANCELLED("CANCELLED");

    companion object {
      fun from(value: String?): STATUS? = values().firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
  }
}