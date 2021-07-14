package com.inventoryorder.rest.response.order

import com.framework.base.BaseResponse
import com.inventoryorder.model.ordersdetails.InventoryOrderModel
import java.io.Serializable

data class InventoryOrderListResponse(
  val Data: InventoryOrderModel? = null,
  val Message: Any? = null,
  val Status: String? = null
) : Serializable, BaseResponse()