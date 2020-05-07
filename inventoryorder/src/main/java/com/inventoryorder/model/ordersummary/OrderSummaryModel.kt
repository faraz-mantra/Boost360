package com.inventoryorder.model.ordersummary

import com.framework.base.BaseResponse
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.util.*
import kotlin.collections.ArrayList

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
    val count: Int? = null,
    var isSelected: Boolean = false
) : BaseResponse(), AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ORDERS_ITEM_TYPE.getLayout()
  }

  fun getOrderType(): ArrayList<OrderSummaryModel> {
    val list = ArrayList<OrderSummaryModel>()
    list.add(OrderSummaryModel(type = OrderType.TOTAL.type, count = TotalOrders, isSelected = true))
    list.add(OrderSummaryModel(type = OrderType.RECEIVED.type, count = TotalOrdersInProgress))
    list.add(OrderSummaryModel(type = OrderType.SUCCESSFUL.type, count = TotalOrdersCompleted))
    list.add(OrderSummaryModel(type = OrderType.CANCELLED.type, count = TotalOrdersCancelled))
    list.add(OrderSummaryModel(type = OrderType.RETURNED.type, count = TotalOrdersEscalated)) //same as ESCALATED
    list.add(OrderSummaryModel(type = OrderType.ABANDONED.type, count = TotalOrdersAbandoned))
    list.add(OrderSummaryModel(type = OrderType.ESCALATED.type, count = TotalOrdersEscalated))
    return list
  }

  enum class OrderType(val type: String, val value: String) {
    TOTAL("All", ""), RECEIVED("Received", OrderStatus.ORDER_INITIATED.name),
    SUCCESSFUL("Successful", OrderStatus.ORDER_COMPLETED.name), CANCELLED("Cancelled", OrderStatus.ORDER_CANCELLED.name),
    RETURNED("Returned", OrderStatus.ESCALATED.name), ABANDONED("Abandoned", "ABANDONED"),
    ESCALATED("Escalated", OrderStatus.ESCALATED.name);

    companion object {
      fun fromType(type: String): OrderType? = values().first { it.type.toLowerCase(Locale.ROOT) == type.toLowerCase(Locale.ROOT) }
      fun fromValue(value: String): OrderType? = values().first { it.value.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  enum class OrderStatus {
    ORDER_INITIATED, PAYMENT_MODE_VERIFIED, PAYMENT_CONFIRMED, ORDER_CONFIRMED, DELIVERY_IN_PROGRESS, DELIVERY_COMPLETED,
    FEEDBACK_PENDING, FEEDBACK_RECEIVED, DELIVERY_DELAYED, DELIVERY_FAILED, ORDER_COMPLETED, ORDER_CANCELLED, ESCALATED;

    companion object {
      fun from(value: String): OrderStatus = values().first { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}

