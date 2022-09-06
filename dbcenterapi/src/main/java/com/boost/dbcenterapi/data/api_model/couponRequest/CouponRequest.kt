package com.boost.dbcenterapi.data.api_model.couponRequest


import com.google.gson.annotations.SerializedName

data class CouponRequest(
  @SerializedName("BulkPropertySegments")
  var bulkPropertySegments: List<List<BulkPropertySegment>>? = null,
  @SerializedName("SchemaId")
  var schemaId: String? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null
)