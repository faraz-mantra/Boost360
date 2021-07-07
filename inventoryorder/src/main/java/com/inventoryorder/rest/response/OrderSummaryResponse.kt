package com.inventoryorder.rest.response

import com.framework.base.BaseResponse
import com.inventoryorder.model.ordersummary.OrderSummaryModel

data class OrderSummaryResponse(
  val Data: OrderSummaryModel? = null,
//    val Message: String? = null,
  val Status: String? = null
) : BaseResponse()