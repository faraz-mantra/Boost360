package com.boost.dbcenterapi.data.api_model.cart

import com.boost.dbcenterapi.data.api_model.couponRequest.BulkPropertySegment
import com.google.gson.annotations.SerializedName

data class RecommendedAddonsRequest(
        @SerializedName("widget-list")
        var widgetList: List<String>? = null,
        @SerializedName("fpid")
        var fpid: String? = null,
        @SerializedName("category")
        var category: String? = null
)
