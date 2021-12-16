package com.boost.dbcenterapi.data.renewalcart


import com.google.gson.annotations.SerializedName

data class CreateCartResponse(
  @SerializedName("Error")
  var error: Error? = null,
  @SerializedName("Result")
  var result: CreateCartResult? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
)