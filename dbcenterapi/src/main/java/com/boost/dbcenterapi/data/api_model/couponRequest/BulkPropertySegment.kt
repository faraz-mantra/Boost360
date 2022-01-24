package com.boost.dbcenterapi.data.api_model.couponRequest


import com.google.gson.annotations.SerializedName

data class BulkPropertySegment(
  @SerializedName("Index")
  var index: Int? = null,
  @SerializedName("Limit")
  var limit: Int? = null,
  @SerializedName("ObjectKeys")
  var objectKeys: ObjectKeys? = null,
  @SerializedName("PropertyDataType")
  var propertyDataType: String? = null,
  @SerializedName("PropertyName")
  var propertyName: String? = null,
  @SerializedName("Type")
  var type: Int? = null
)