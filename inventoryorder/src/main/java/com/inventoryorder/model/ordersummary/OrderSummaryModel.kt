package com.inventoryorder.model.ordersummary

import com.framework.base.BaseResponse
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class OrderSummaryModel(
    val CurrencyCode: String? = null,
    val SellerId: String? = null,
    val TotalCompletedOrders: Int? = null,
    val TotalGrossAmount: Double? = null,
    val TotalNetAmount: Double? = null,
    val TotalOrders: Int? = null,
    val TotalOrdersAbandoned: Int? = null,
    val TotalOrdersCancelled: Int? = null,
    val TotalOrdersCompleted: Int? = null,
    val TotalOrdersEscalated: Int? = null,
    val TotalOrdersInProgress: Int? = null,
    val TotalRevenue: Double? = null,

    val type: String? = null,
    val count: Int? = null
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ORDER_ITEM_TYPE.getLayout()
  }

  fun getOrderType(): ArrayList<OrderSummaryModel> {
    val list = ArrayList<OrderSummaryModel>()
    list.add(OrderSummaryModel(type = OrderType.TOTAL.type, count = TotalOrders))
    list.add(OrderSummaryModel(type = OrderType.RECEIVED.type, count = TotalOrdersInProgress))
    list.add(OrderSummaryModel(type = OrderType.SUCCESSFUL.type, count = TotalOrdersCompleted))
    list.add(OrderSummaryModel(type = OrderType.CANCELLED.type, count = TotalOrdersCancelled))
    list.add(OrderSummaryModel(type = OrderType.RETURNED.type, count = 0))
    list.add(OrderSummaryModel(type = OrderType.ABANDONED.type, count = TotalOrdersAbandoned))
    list.add(OrderSummaryModel(type = OrderType.ESCALATED.type, count = TotalOrdersEscalated))
    return list
  }

  enum class OrderType(val type: String) {
    TOTAL("All"), RECEIVED("Received"), SUCCESSFUL("Successful"), CANCELLED("Cancelled"),
    RETURNED("Returned"), ABANDONED("Abandoned"), ESCALATED("Escalated");

    companion object {
      fun from(findValue: String): OrderType = values().first { it.type == findValue }
    }
  }
}

