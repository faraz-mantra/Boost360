package com.inventoryorder.model.orderfilter

data class OrderFilterRequest(
  var filterBy: ArrayList<OrderFilterRequestItem> = ArrayList(),
  var clientId: String? = null,
  var limit: Int? = null,
  var skip: Int? = null
)