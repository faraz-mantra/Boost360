package com.inventoryorder.model.ordersummary

import java.util.*

enum class OrderStatusValue(val type: String, val status: String, val value: String) {
  ORDER_INITIATED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Order Initiated"),
  PAYMENT_MODE_VERIFIED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Order Placed"),
  PAYMENT_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Order Placed"),
  ORDER_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Order Confirmed"),
  DELIVERY_IN_PROGRESS_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Delivery In-Progress"),
  DELIVERY_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Order Delivered"),
  FEEDBACK_PENDING_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Order Delivered"),
  FEEDBACK_RECEIVED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Order Delivered"),
  DELIVERY_DELAYED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Order Delayed"),
  DELIVERY_FAILED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Order Failed"),
  ORDER_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Order Delivered"),
  ORDER_CANCELLED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Order Escalated"),

  ORDER_INITIATED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Apt Initiated"),
  PAYMENT_MODE_VERIFIED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Apt Requested"),
  PAYMENT_CONFIRMED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Apt Requested"),
  ORDER_CONFIRMED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Apt Confirmed"),
  DELIVERY_IN_PROGRESS_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Apt In-Progress"),
  DELIVERY_COMPLETED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Apt Completed"),
  FEEDBACK_PENDING_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Apt Completed"),
  FEEDBACK_RECEIVED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Apt Completed"),
  DELIVERY_DELAYED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Apt Delayed"),
  DELIVERY_FAILED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Apt Failed"),
  ORDER_COMPLETED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Apt Completed"),
  ORDER_CANCELLED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Apt Escalated"),

  ORDER_INITIATED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Consult Initiated"),
  PAYMENT_MODE_VERIFIED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Consult Requested"),
  PAYMENT_CONFIRMED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Consult Requested"),
  ORDER_CONFIRMED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Consult Confirmed"),
  DELIVERY_IN_PROGRESS_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Consult In-Progress"),
  DELIVERY_COMPLETED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Consult Completed"),
  FEEDBACK_PENDING_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Consult Completed"),
  FEEDBACK_RECEIVED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Consult Completed"),
  DELIVERY_DELAYED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Consult Delayed"),
  DELIVERY_FAILED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Consult Failed"),
  ORDER_COMPLETED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Consult Completed"),
  ORDER_CANCELLED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Consult Escalated");


  companion object {
    fun fromStatusOrder(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.toLowerCase(Locale.ROOT) == status.toLowerCase(Locale.ROOT) && it.type == OrderSummaryModel.OrderType.ORDER.name)
    }

    fun fromStatusAppointment(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.toLowerCase(Locale.ROOT) == status.toLowerCase(Locale.ROOT) && it.type == OrderSummaryModel.OrderType.APPOINTMENT.name)
    }

    fun fromStatusConsultation(status: String): OrderStatusValue? = values().firstOrNull {
      (it.status.toLowerCase(Locale.ROOT) == status.toLowerCase(Locale.ROOT) && it.type == OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name)
    }
  }
}