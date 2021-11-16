package com.boost.cart.data.api_model.customerId.get

data class Result(
  val AddressDetails: AddressDetails,
  val BusinessDetails: BusinessDetails,
  val ClientId: String,
  val CountryCode: String,
  val CreatedOn: String,
  val DeviceType: String,
  val Email: String,
  val ExternalSourceId: String,
  val InternalSourceId: String,
  val IsArchived: Boolean,
  val MobileNumber: String,
  val Name: String? = null,
  val TaxDetails: TaxDetails,
  val UpdatedOn: String,
  val _id: String
)