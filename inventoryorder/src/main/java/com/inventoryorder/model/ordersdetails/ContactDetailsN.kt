package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class ContactDetailsN(
  val EmailId: String? = null,
  val FullName: String? = null,
  val PrimaryContactNumber: String? = null,
  val SecondaryContactNumber: String? = null
) : Serializable