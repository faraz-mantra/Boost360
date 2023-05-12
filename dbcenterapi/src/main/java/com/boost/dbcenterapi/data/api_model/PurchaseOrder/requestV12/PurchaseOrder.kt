package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV12

data class PurchaseOrder(
    val Bundle: String? = null,
    val CouponCode: String? = null,
    val Discount: Double,
    val Expiry: Expiry,
    val Features: List<String>,
    val NetPrice: Double,
    val PrePostPurchase: List<PrePostPurchase>
)