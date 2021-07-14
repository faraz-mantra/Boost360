package com.inventoryorder.model.ordersummary

import com.inventoryorder.R

enum class OrderStatusValue(
  val type: String,
  val status: String,
  val value: String,
  val icon: Int = 0
) {
  ORDER_INITIATED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ORDER_INITIATED.name,
    "Initiated",
    R.drawable.ic_order_initiated
  ),
  PAYMENT_MODE_VERIFIED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name,
    "Placed",
    R.drawable.ic_order_placed
  ),
  PAYMENT_CONFIRMED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name,
    "Placed",
    R.drawable.ic_order_placed
  ),
  ORDER_CONFIRMED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name,
    "Confirmed",
    R.drawable.ic_order_confirmed
  ),
  DELIVERY_IN_PROGRESS_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name,
    "Delivery in progress",
    R.drawable.ic_order_transit
  ),
  DELIVERY_COMPLETED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name,
    "Delivery completed",
    R.drawable.ic_order_ready
  ),
  FEEDBACK_PENDING_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name,
    "Delivered",
    R.drawable.ic_order_ready
  ),
  FEEDBACK_RECEIVED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name,
    "Completed",
    R.drawable.ic_order_ready
  ),
  DELIVERY_DELAYED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name,
    "Delivery Delayed",
    R.drawable.ic_order_canceled
  ),
  DELIVERY_FAILED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name,
    "Delivery Failed",
    R.drawable.ic_order_canceled
  ),
  ORDER_COMPLETED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name,
    "Delivery completed",
    R.drawable.ic_order_ready
  ),
  ORDER_CANCELLED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name,
    "Cancelled By",
    R.drawable.ic_order_canceled
  ),
  ESCALATED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ESCALATED.name,
    "Order Escalated",
    R.drawable.ic_order_canceled
  ),
  ABANDONED_1(
    OrderSummaryModel.OrderType.ORDER.name,
    OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name,
    "Abandoned",
    R.drawable.ic_order_canceled
  ),

  ORDER_INITIATED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.ORDER_INITIATED.name,
    "Initiated",
    R.drawable.ic_order_initiated
  ),
  PAYMENT_MODE_VERIFIED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name,
    "Booked",
    R.drawable.ic_inprogress
  ),
  PAYMENT_CONFIRMED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name,
    "Booked",
    R.drawable.ic_inprogress
  ),
  ORDER_CONFIRMED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name,
    "Confirmed",
    R.drawable.ic_order_confirmed
  ),
  DELIVERY_IN_PROGRESS_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name,
    "In Progress",
    R.drawable.ic_inprogress
  ),
  DELIVERY_COMPLETED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name,
    "CUSTOMER SERVED",
    R.drawable.ic_done
  ),
  FEEDBACK_PENDING_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name,
    "CUSTOMER SERVED",
    R.drawable.ic_done
  ),
  FEEDBACK_RECEIVED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name,
    "Feedback Received",
    R.drawable.ic_chat_bubble
  ),
  DELIVERY_DELAYED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name,
    "Delayed",
    R.drawable.ic_order_canceled
  ),
  DELIVERY_FAILED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name,
    "Failed",
    R.drawable.ic_order_canceled
  ),
  ORDER_COMPLETED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name,
    "CUSTOMER SERVED",
    R.drawable.ic_done
  ),
  ORDER_CANCELLED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name,
    "Cancelled By",
    R.drawable.ic_order_canceled
  ),
  ESCALATED_2(
    OrderSummaryModel.OrderType.APPOINTMENT.name,
    OrderSummaryModel.OrderStatus.ESCALATED.name,
    "Escalated",
    R.drawable.ic_order_canceled
  ),


  ORDER_INITIATED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.ORDER_INITIATED.name,
    "Initiated"
  ),
  PAYMENT_MODE_VERIFIED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name,
    "Booked"
  ),
  PAYMENT_CONFIRMED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name,
    "Booked"
  ),
  ORDER_CONFIRMED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name,
    "Confirmed"
  ),
  DELIVERY_IN_PROGRESS_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name,
    "In Progress"
  ),
  DELIVERY_COMPLETED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name,
    "Completed"
  ),
  FEEDBACK_PENDING_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name,
    "CUSTOMER SERVED"
  ),
  FEEDBACK_RECEIVED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name,
    "CUSTOMER SERVED"
  ),
  DELIVERY_DELAYED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name,
    "Delayed"
  ),
  DELIVERY_FAILED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name,
    "Failed"
  ),
  ORDER_COMPLETED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name,
    "CUSTOMER SERVED"
  ),
  ORDER_CANCELLED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name,
    "Cancelled By"
  ),
  ESCALATED_3(
    OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name,
    OrderSummaryModel.OrderStatus.ESCALATED.name,
    "Escalated"
  );


  companion object {
    fun fromStatusOrder(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.equals(
        status,
        ignoreCase = true
      ) && it.type == OrderSummaryModel.OrderType.ORDER.name)
    }

    fun fromStatusAppointment(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.equals(
        status,
        ignoreCase = true
      ) && it.type == OrderSummaryModel.OrderType.APPOINTMENT.name)
    }

    fun fromStatusConsultation(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.equals(
        status,
        ignoreCase = true
      ) && it.type == OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name)
    }

  }
}