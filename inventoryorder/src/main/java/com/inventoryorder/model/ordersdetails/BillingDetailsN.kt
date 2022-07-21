package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class BillingDetailsN(
  val AmountPayableByBuyer: Double? = null,
  val AmountPayableToSeller: Double? = null,
  val AssuredPurchaseCharges: Double? = null,
  val CurrencyCode: String? = null,
  val DiscountAmount: Double? = null,
  val ExtraProperties: Any? = null,
  val GrossAmount: Double? = null,
  val GstCharges: Double? = null,
  val InvoiceFileName: String? = null,
  val InvoiceUrl: String? = null,
  val NFDeliveryCharges: Double? = null,
  val SellerDeliveryCharges: Double? = null,
  var NetAmount: Double? = null,
  var TaxDetails: Any? = null,
  val TransactionCharges: Double? = null,
) : Serializable {

  fun getCurrencyCodeValue(): String {
    return if (CurrencyCode.isNullOrEmpty().not()) CurrencyCode!! else "INR"
  }
}