package com.boost.dbcenterapi.data.api_model.autorenew

data class OrderAutoRenewRequest(
    val autoRenewal: Boolean,
    val clientId: String,
    val floatingPointId: String,
    val orderId: String,
    val period: String, // year/month
    val interval: Int // 3  or 1 (year
)