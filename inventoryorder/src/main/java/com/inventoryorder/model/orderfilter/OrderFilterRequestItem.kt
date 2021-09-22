package com.inventoryorder.model.orderfilter

data class OrderFilterRequestItem(
  var QueryObject: ArrayList<QueryObject>? = ArrayList(),
  var QueryConditionType: String? = null
) {
  enum class Condition {
    AND, OR
  }
}