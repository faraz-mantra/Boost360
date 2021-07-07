package com.inventoryorder.model.summary.request

import com.google.gson.annotations.SerializedName

data class QueryObject(
  @SerializedName("Key")
  var key: String? = null,
  @SerializedName("QueryOperator")
  var queryOperator: String? = null,
  @SerializedName("Value")
  var value: String? = null,
) {
  enum class Operator {
    EQ, NE, GT, LT, GTE, LTE
  }

  enum class keys {
    CreatedOn,
  }
}