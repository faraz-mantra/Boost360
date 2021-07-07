package com.inventoryorder.model.ordersummary

import com.framework.base.BaseRequest

data class OrderSummaryRequest(
  var clientId: String? = null,
  var sellerId: String? = null,
  var orderStatus: String? = null,
  var paymentStatus: String? = null,
  var skip: Int? = null,
  var limit: Int? = null,
  var orderMode: String? = null,
  var deliveryMode: String? = null
) : BaseRequest() {

  enum class OrderMode {
    DELIVERY, PICKUP, APPOINTMENT
  }

  enum class DeliveryMode {
    ONLINE, OFFLINE
  }
}