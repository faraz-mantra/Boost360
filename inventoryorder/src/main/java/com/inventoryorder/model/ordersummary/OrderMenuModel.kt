package com.inventoryorder.model.ordersummary

import com.framework.base.BaseResponse
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class OrderMenuModel(
    var type: String? = null,
    var endLine: Boolean = true,
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ORDER_MENU_ITEM.getLayout()
  }


  fun getOrderMenu(orderItem: OrderItem?): ArrayList<OrderMenuModel> {
    val list = ArrayList<OrderMenuModel>()
    orderItem?.orderBtnStatus()?.forEach { list.add(OrderMenuModel(type = it.name)) }
    if (list.isNotEmpty()) list[list.size - 1].endLine = false
    return list
  }

  fun getAppointmentMenu(orderItem: OrderItem?): ArrayList<OrderMenuModel> {
    val list = ArrayList<OrderMenuModel>()
    orderItem?.appointmentButtonStatus()?.forEach { list.add(OrderMenuModel(type = it.name)) }
    if (list.isNotEmpty()) list[list.size - 1].endLine = false
    return list
  }

  enum class MenuStatus(var title: String, var color: Int) {
    CONFIRM_ORDER("Confirm Order", R.color.black_4a4a4a),
    CANCEL_ORDER("Cancel Order", R.color.watermelon_light_10),
    MARK_AS_DELIVERED("Mark as delivered", R.color.black_4a4a4a),
    MARK_AS_SHIPPED("Mark as shipped", R.color.black_4a4a4a),

    REQUEST_PAYMENT("Request Payment", R.color.black_4a4a4a),
    MARK_PAYMENT_DONE("Mark Payment Done", R.color.black_4a4a4a),
    REQUEST_FEEDBACK("Request Feedback", R.color.black_4a4a4a),
    SEND_RE_BOOKING("Re-Booking Reminder", R.color.black_4a4a4a),

    CONFIRM_APPOINTMENT("Confirm Appointment", R.color.black_4a4a4a),
    CANCEL_APPOINTMENT("Cancel Appointment", R.color.watermelon_light_10),
    MARK_AS_SERVED("Mark as Served", R.color.black_4a4a4a),
    START_APPOINTMENT("Start Appointment", R.color.black_4a4a4a);

    companion object {
      fun from(type: String?): MenuStatus? = values().firstOrNull { it.name.equals(type, ignoreCase = true) }
    }
  }
}

