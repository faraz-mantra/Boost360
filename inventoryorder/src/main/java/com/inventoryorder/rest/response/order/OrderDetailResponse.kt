package com.inventoryorder.rest.response.order

import com.framework.base.BaseResponse
import com.inventoryorder.model.ordersdetails.OrderItem
import java.io.Serializable

data class OrderDetailResponse(
    val Data: OrderItem? = null,
    val Message: Any? = null,
    val Status: String? = null
) : Serializable, BaseResponse()