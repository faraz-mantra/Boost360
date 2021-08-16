package com.inventoryorder.model.ordersdetails

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class InventoryOrderModel(
  val delivery: String? = null,
  val orderType: String? = null,
  val Items: ArrayList<OrderItem>? = null,
  val Count: Int? = null,
  val Limit: Int? = null,
  val Skip: Int? = null,
  val Total: Int? = null
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.INVENTORY_ORDER_ITEM.getLayout()
  }

  fun total(): Int {
    return Total ?: 0
  }

  fun getOrderItem(): ArrayList<InventoryOrderModel> {
    val list = ArrayList<InventoryOrderModel>()
    list.add(InventoryOrderModel("Pickup", TYPE.NEW_ORDER.name))
    list.add(InventoryOrderModel("Home Delivery", TYPE.CANCELLED.name))
    list.add(InventoryOrderModel("Pickup", TYPE.DELAYED.name))
    list.add(InventoryOrderModel("Home Delivery", TYPE.COMPLETED.name))
    list.add(InventoryOrderModel("Home Delivery", TYPE.CANCELLED.name))
    return list
  }

  enum class TYPE {
    NEW_ORDER, CANCELLED, COMPLETED, DELAYED
  }
}

