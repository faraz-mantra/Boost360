package com.boost.payment.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class GetLastPaymentDetails(
  @SerializedName("Error")
  var error: Error? = null,
  @SerializedName("Result")
  var result: Result? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
)