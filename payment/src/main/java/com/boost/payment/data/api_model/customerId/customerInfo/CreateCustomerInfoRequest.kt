package com.boost.payment.data.api_model.customerId.customerInfo

data class CreateCustomerInfoRequest(
  val AddressDetails: AddressDetails?,
  val BusinessDetails: BusinessDetails?,
  val ClientId: String?,
  val CountryCode: String?,
  val DeviceType: String?,
  val Email: String?,
  val InternalSourceId: String?,
  val MobileNumber: String?,
  val Name: String?,
  val TaxDetails: TaxDetails?
)