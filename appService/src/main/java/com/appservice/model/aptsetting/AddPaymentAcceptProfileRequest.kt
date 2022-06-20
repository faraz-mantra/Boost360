package com.appservice.model.aptsetting

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddPaymentAcceptProfileRequest(
  @SerializedName("AcceptPaymentAfterBooking")
  var acceptPaymentAfterBooking: Boolean? = null,
  @SerializedName("AcceptPaymentDuringBooking")
  var acceptPaymentDuringBooking: Boolean? = null,
  @SerializedName("ClientId")
  var clientId: String? = null,
  @SerializedName("FloatingPointId")
  var floatingPointId: String? = null
) : BaseRequest(), Serializable