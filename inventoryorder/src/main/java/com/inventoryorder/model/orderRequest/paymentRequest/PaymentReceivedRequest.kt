package com.inventoryorder.model.orderRequest.paymentRequest

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentReceivedRequest(

    @field:SerializedName("OnlinePaymentProvider")
    val onlinePaymentProvider: String? = null,

    @field:SerializedName("Remarks")
    val remarks: String? = null,

    @field:SerializedName("PaymentMethod")
    val paymentMethod: String? = null,

    @field:SerializedName("OrderId")
    val orderId: String? = null,

    @field:SerializedName("TransactionId")
    val transactionId: String? = "NA",

    ) : BaseRequest(), Serializable {

  enum class PaymentMethod {
    COD, ONLINEPAYMENT, FREE
  }

  enum class PaymentProvider {
    cash, card, upi, others
  }
}
