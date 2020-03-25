package com.boost.upgrades.data.api_model.customerId.get

data class Result(
    val ClientId: String,
    val CreatedOn: String,
    val DeviceType: Int,
    val Email: String,
    val ExternalSourceId: String,
    val InternalSourceId: String,
    val MobileNumber: String,
    val Name: String,
    val PaymentChannel: Int,
    val _id: String
)