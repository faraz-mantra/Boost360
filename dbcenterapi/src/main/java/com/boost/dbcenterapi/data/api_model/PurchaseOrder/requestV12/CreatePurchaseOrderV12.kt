package com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV12

data class CreatePurchaseOrderV12(
    val CartStateId: String,
    val ClientId: String,
    val FloatingPointId: String,
    val PaymentDetails: PaymentDetails,
    val PurchaseOrderType: String,
    val PurchaseOrders: List<PurchaseOrder>
)