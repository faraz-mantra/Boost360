package com.appservice.model.serviceProduct.update

import java.io.Serializable
import java.util.*

data class ProductUpdate(
  var clientId: String? = null,
  var productId: String? = null,
  var productType: String? = null,
  var updates: ArrayList<UpdateValue>? = null
) : Serializable