package com.appservice.model.paymentKyc.update


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateKycValue(
  @SerializedName("$".plus("set"))
  var `set`: KycSet? = null
) : Serializable