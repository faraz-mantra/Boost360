package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.util.*
import kotlin.collections.ArrayList

class FilterModel(
  val type: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.FILTER_ORDER_ITEM.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected }
      ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.warm_grey_31 } ?: R.color.white
  }

  fun getDataOrders(): ArrayList<FilterModel> {
    val list = ArrayList<FilterModel>()
    list.add(FilterModel(FilterType.ALL_ORDERS.type, true))
    list.add(FilterModel(FilterType.PLACED.type))
    list.add(FilterModel(FilterType.CONFIRMED.type))
    list.add(FilterModel(FilterType.SHIPPED.type))
    list.add(FilterModel(FilterType.DELIVERED_ORDER.type))
    list.add(FilterModel(FilterType.CANCELLED_ORDER.type))
    return list
  }

  fun getDataAppointments(): ArrayList<FilterModel> {
    val list = ArrayList<FilterModel>()
    list.add(FilterModel(FilterType.ALL_APPOINTMENTS.type, true))
    list.add(FilterModel(FilterType.CONFIRM.type))
    list.add(FilterModel(FilterType.DELIVERED.type))
    list.add(FilterModel(FilterType.CANCELLED.type))
    return list
  }

  fun getDataConsultations(): ArrayList<FilterModel> {
    val list = ArrayList<FilterModel>()
    list.add(FilterModel(FilterType.ALL_CONSULTATIONS.type, true))
    list.add(FilterModel(FilterType.UPCOMING_CONSULT.type))
    list.add(FilterModel(FilterType.COMPLETED_CONSULTATIONS.type))
    list.add(FilterModel(FilterType.CANCEL_CONSULTATIONS.type))
    return list
  }

  enum class FilterType(val type: String, val value: String) {
    //TODO for Appointment
    ALL_APPOINTMENTS("All appointments", ""),
    CONFIRM("Confirmed", OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name),
    DELIVERED("Delivered", OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name),
    CANCELLED("Cancelled", OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name),

    //TODO for Consultation
    ALL_CONSULTATIONS("All consultations", ""),
    UPCOMING_CONSULT("Upcoming consultations", OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name),
    COMPLETED_CONSULTATIONS(
      "Completed consultations",
      OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name
    ),
    CANCEL_CONSULTATIONS(
      "Cancel consultations",
      OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name
    ),

    //TODO for order
    ALL_ORDERS("All orders", ""),
    PLACED("Placed", OrderSummaryModel.OrderStatus.ORDER_INITIATED.name),
    CONFIRMED("Confirmed", OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name),
    SHIPPED("Shipped", OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name),
    DELIVERED_ORDER("Delivered", OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name),
    CANCELLED_ORDER("Cancelled", OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name);

    companion object {
      fun fromType(type: String): FilterType? =
        values().firstOrNull { it.type.toLowerCase(Locale.ROOT) == type.toLowerCase(Locale.ROOT) }

      fun fromValue(value: String): FilterType? =
        values().firstOrNull { it.value.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }
}