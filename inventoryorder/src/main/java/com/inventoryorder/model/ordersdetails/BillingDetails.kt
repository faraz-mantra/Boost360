package com.inventoryorder.model.ordersdetails

data class BillingDetails(
    val AmountPayableByBuyer: Double? = null,
    val AmountPayableToSeller: Double? = null,
    val AssuredPurchaseCharges: Double? = null,
    val CurrencyCode: String? = null,
    val DiscountAmount: Double? = null,
    val GrossAmount: Double? = null,
    val GstCharges: Double? = null,
    val InvoiceFileName: String? = null,
    val InvoiceUrl: String? = null,
    val NFDeliveryCharges: Double? = null,
    val SellerDeliveryCharges: Double? = null,
    val TransactionCharges: Double? = null
)