package com.boost.upgrades.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class RegisteredBusinessContactDetails(
  @SerializedName("MerchantName")
  var merchantName: Any? = null,
  @SerializedName("RegisteredBusinessCountryCode")
  var registeredBusinessCountryCode: Any? = null,
  @SerializedName("RegisteredBusinessEmail")
  var registeredBusinessEmail: Any? = null,
  @SerializedName("RegisteredBusinessMobile")
  var registeredBusinessMobile: Any? = null
)