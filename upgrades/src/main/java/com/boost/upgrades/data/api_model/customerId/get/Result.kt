package com.boost.upgrades.data.api_model.customerId.get

data class Result(
    val ClientId: String,
    val CreatedOn: String,
    val DeviceType: String,
    val Email: String,
    val ExternalSourceId: String,
    val InternalSourceId: String,
    val MobileNumber: String,
    val Name: String,
    val PaymentChannel: String,
    val UpdatedOn: String,
    val IsArchived: Boolean,
    val TaxDetails: TaxDetails,
    val _id: String
)