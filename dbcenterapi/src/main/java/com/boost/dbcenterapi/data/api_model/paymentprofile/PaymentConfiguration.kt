package com.boost.dbcenterapi.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class PaymentConfiguration(
  @SerializedName("AcceptCodForHomeDelivery")
  var acceptCodForHomeDelivery: Boolean? = null,
  @SerializedName("AcceptCodForStorePickup")
  var acceptCodForStorePickup: Boolean? = null
)