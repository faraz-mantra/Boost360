package com.boost.dbcenterapi.data.renewalcart


import com.google.gson.annotations.SerializedName

data class RenewalPurchasedResponse(
  @SerializedName("Error")
  var error: Error? = null,
  @SerializedName("Result")
  var result: List<RenewalResult>? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
)