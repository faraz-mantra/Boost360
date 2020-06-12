package com.inventoryorder.model.ordersummary

import java.util.*

enum class OrderStatusValue(val type: String, val status: String, val value: String) {
  ORDER_INITIATED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Order Confirmed"),
  PAYMENT_MODE_VERIFIED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Order Requested"),
  PAYMENT_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Order Requested"),
  ORDER_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Order Confirmed"),
  DELIVERY_IN_PROGRESS_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Order Progress"),
  DELIVERY_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Order Completed"),
  FEEDBACK_PENDING_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Order Completed"),
  FEEDBACK_RECEIVED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Order Completed"),
  DELIVERY_DELAYED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Order Delivery Delayed"),
  DELIVERY_FAILED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Order Delivery Failed"),
  ORDER_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Order Completed"),
  ORDER_CANCELLED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Escalated"),

  ORDER_INITIATED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Apt Initiated"),
  PAYMENT_MODE_VERIFIED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Apt Requested"),
  PAYMENT_CONFIRMED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Apt Requested"),
  ORDER_CONFIRMED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Apt Confirmed"),
  DELIVERY_IN_PROGRESS_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Apt Progress"),
  DELIVERY_COMPLETED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Apt Completed"),
  FEEDBACK_PENDING_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Apt Completed"),
  FEEDBACK_RECEIVED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Apt Completed"),
  DELIVERY_DELAYED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Apt Delivery Delayed"),
  DELIVERY_FAILED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Apt Delivery Failed"),
  ORDER_COMPLETED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Apt Completed"),
  ORDER_CANCELLED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_2(OrderSummaryModel.OrderType.APPOINTMENT.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Escalated"),

  ORDER_INITIATED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Apt Initiated"),
  PAYMENT_MODE_VERIFIED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Apt Requested"),
  PAYMENT_CONFIRMED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Apt Requested"),
  ORDER_CONFIRMED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Apt Confirmed"),
  DELIVERY_IN_PROGRESS_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "Apt Progress"),
  DELIVERY_COMPLETED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Apt Completed"),
  FEEDBACK_PENDING_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Apt Completed"),
  FEEDBACK_RECEIVED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Apt Completed"),
  DELIVERY_DELAYED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Apt Delivery Delayed"),
  DELIVERY_FAILED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Apt Delivery Failed"),
  ORDER_COMPLETED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Apt Completed"),
  ORDER_CANCELLED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled By"),
  ESCALATED_3(OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name, OrderSummaryModel.OrderStatus.ESCALATED.name, "Escalated");


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