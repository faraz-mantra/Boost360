package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class SettlementDetailsN(
  val ExtraProperties: Any? = null,
  val ReferenceId: String? = null,
  val Status: String? = null,
  val TransferDate: String? = null,
  val TransactionAmount: Double? = null,
  val MerchantDetails: Any? = null,
  val SettlementDocuments: Any? = null,
) : Serializable