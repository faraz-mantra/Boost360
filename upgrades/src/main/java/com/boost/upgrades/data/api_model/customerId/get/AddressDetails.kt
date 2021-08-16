package com.boost.upgrades.data.api_model.customerId.get

data class AddressDetails(
  val City: String,
  val Country: String,
  val Line1: String? = null,
  val Line2: Any,
  val State: String? = "",
  val ZipCode: Any
)