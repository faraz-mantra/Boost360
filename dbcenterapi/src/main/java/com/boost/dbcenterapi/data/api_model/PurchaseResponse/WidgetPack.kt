package com.boost.dbcenterapi.data.api_model.PurchaseResponse

data class WidgetPack(
    val CreatedOn: String,
    val Desc: String,
    val Discount: Double,
    val ExpiryDate: String,
    val ExpiryInDays: Int,
    val ExpiryInMths: Int,
    val Name: String,
    val Price: Double,
    val Properties: Any,
    val WidgetKey: String
)