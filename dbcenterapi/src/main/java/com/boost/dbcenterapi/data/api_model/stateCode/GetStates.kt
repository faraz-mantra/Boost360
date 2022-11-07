package com.boost.dbcenterapi.data.api_model.stateCode


import com.google.gson.annotations.SerializedName

data class GetStates(
  @SerializedName("Error")
  var error: Error? = null,
  @SerializedName("Result")
  var result: Result? = null,
  @SerializedName("StatusCode")
  var statusCode: Int? = null
)