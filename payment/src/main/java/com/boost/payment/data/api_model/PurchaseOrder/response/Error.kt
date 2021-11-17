package com.boost.payment.data.api_model.PurchaseOrder.response

data class Error(
  val ErrorCode: Any,
  val ErrorList: ErrorList
)