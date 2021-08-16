package com.inventoryorder.model.summary.request

import com.google.gson.annotations.SerializedName

data class FilterBy(
  @SerializedName("QueryConditionType")
  var queryConditionType: String? = null,
  @SerializedName("QueryObject")
  var queryObject: ArrayList<QueryObject>? = null
) {
  enum class ConditionType {
    AND, OR
  }
}