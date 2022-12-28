package com.boost.dbcenterapi.data.api_model.cart

data class Recommendation(
    var purchaseCount: Int? = null,
    var purchaseCountWithCartItems: Int? = null,
    var widgetName: String? = null
)