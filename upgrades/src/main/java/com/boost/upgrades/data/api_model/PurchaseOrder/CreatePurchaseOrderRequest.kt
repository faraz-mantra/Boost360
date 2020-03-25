package com.boost.upgrades.data.api_model.PurchaseOrder

data class CreatePurchaseOrderRequest(
    val ClientId: String,
    val FpId: String,
    val PaymentDetails: PaymentDetails,
    val Widgets: List<Widget>
)