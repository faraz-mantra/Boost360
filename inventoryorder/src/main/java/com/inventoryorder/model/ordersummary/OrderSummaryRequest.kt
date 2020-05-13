package com.inventoryorder.model.ordersummary

import com.framework.base.BaseRequest

data class OrderSummaryRequest(
    var clientId: String? = null,
    var sellerId: String? = null,
    var orderStatus: String? = null,
    var skip: Int? = null,
    var limit: Int? = null
) : BaseRequest()