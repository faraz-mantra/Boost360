package com.boost.upgrades.data.api_model.PurchaseOrder

data class PaymentDetails(
    val Discount: Double,
    val GSTIN: String,
    val PrimaryPaymentChannel: Int,
    val SecondaryPaymentChannel: Int,
    val TDS: Int,
    val TanNumber: Int,
    val Tax: Int,
    val TotalPrice: Double
)