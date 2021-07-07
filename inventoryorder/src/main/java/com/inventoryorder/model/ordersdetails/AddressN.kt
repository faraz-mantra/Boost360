package com.inventoryorder.model.ordersdetails

import java.io.Serializable

data class AddressN(
  val AddressLine1: String? = null,
  val AddressLine2: String? = null,
  val City: String? = null,
  val Country: String? = null,
  val Region: String? = null,
  val Zipcode: String? = null
) : Serializable {

  fun addressLine1(): String {
    return AddressLine1 ?: ""
  }
}