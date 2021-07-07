package com.inventoryorder.model.order

import java.io.Serializable

data class UniquePaymentUrl(
  val description: String? = null,
  val url: String? = null
) : Serializable