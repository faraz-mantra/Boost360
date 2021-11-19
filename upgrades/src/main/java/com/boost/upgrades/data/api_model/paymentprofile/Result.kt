package com.boost.upgrades.data.api_model.paymentprofile


import com.google.gson.annotations.SerializedName

data class Result(
  @SerializedName("AdditionalKYCDocuments")
  var additionalKYCDocuments: Any? = null,
  @SerializedName("BankAccountDetails")
  var bankAccountDetails: Any? = null,
  @SerializedName("LastPaymentMethodDetails")
  var lastPaymentMethodDetails: LastPaymentMethodDetails? = null,
  @SerializedName("MerchantSignature")
  var merchantSignature: Any? = null,
  @SerializedName("PaymentConfiguration")
  var paymentConfiguration: PaymentConfiguration? = null,
  @SerializedName("PaymentGatewayDetails")
  var paymentGatewayDetails: List<Any>? = null,
  @SerializedName("RegisteredBusinessAddress")
  var registeredBusinessAddress: RegisteredBusinessAddress? = null,
  @SerializedName("RegisteredBusinessContactDetails")
  var registeredBusinessContactDetails: RegisteredBusinessContactDetails? = null,
  @SerializedName("TaxDetails")
  var taxDetails: Any? = null,
  @SerializedName("UPIId")
  var uPIId: Any? = null
)