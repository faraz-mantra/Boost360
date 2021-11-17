package com.boost.payment.data.api_model.GetPurchaseOrder

data class Error(
  val ErrorCode: Any,
  val ErrorList: ErrorList
)