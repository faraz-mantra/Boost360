package com.boost.dbcenterapi.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class LastPaymentMethodDetails(
  @SerializedName("ExtraProperties")
  var extraProperties: ExtraProperties? = null,
  @SerializedName("LastPaymentMethod")
  var lastPaymentMethod: String? = null
)