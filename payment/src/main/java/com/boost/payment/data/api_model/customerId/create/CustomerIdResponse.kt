package com.boost.payment.data.api_model.customerId.create

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerIdResponse(
  @SerializedName("CustomerId")
  @Expose
  val CustomerId: String
)