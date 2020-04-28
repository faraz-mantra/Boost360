package com.inventoryorder.model

import com.framework.base.BaseResponse
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class OrderTypeModel(
    val id: Int? = null,
    val title: String? = null,
    val count: Int? = null
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ORDER_ITEM_TYPE.getLayout()
  }

  fun getOrderType(): ArrayList<OrderTypeModel> {
    val list = ArrayList<OrderTypeModel>()
    list.add(OrderTypeModel(1, "All", 40))
    list.add(OrderTypeModel(1, "Open", 10))
    list.add(OrderTypeModel(1, "Delayed", 10))
    list.add(OrderTypeModel(1, "Completed", 10))
    list.add(OrderTypeModel(1, "Canceled", 10))
    return list
  }
}

