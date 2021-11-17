package com.boost.payment.data.api_model.customerId.get

data class GetCustomerIDResponse(
  val Error: Error,
  val Result: Result,
  val StatusCode: Int
)