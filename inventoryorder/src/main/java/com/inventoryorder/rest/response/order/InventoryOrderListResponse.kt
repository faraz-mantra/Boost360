package com.inventoryorder.rest.response.order

import com.framework.base.BaseResponse
import com.inventoryorder.model.ordersdetails.InventoryOrderModel

data class InventoryOrderListResponse(
    val Data: InventoryOrderModel? = null,
//    val Message: String,
    val Status: String? = null
) : BaseResponse()