package com.inventoryorder.model.ordersummary

import com.inventoryorder.R

enum class OrderStatusValue(val type: String, val status: String, val value: String, val icon: Int = 0) {
    ORDER_INITIATED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_INITIATED.name, "Initiated", R.drawable.ic_order_initiated),
    PAYMENT_MODE_VERIFIED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED.name, "Placed",R.drawable.ic_order_placed),
    PAYMENT_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED.name, "Placed",R.drawable.ic_order_placed),
    ORDER_CONFIRMED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name, "Confirmed",R.drawable.ic_order_confirmed),
    DELIVERY_IN_PROGRESS_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS.name, "In-Progress",R.drawable.ic_order_transit),
    DELIVERY_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED.name, "Order Completed",R.drawable.ic_order_ready),
    FEEDBACK_PENDING_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_PENDING.name, "Order Completed",R.drawable.ic_order_ready),
    FEEDBACK_RECEIVED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED.name, "Order Completed",R.drawable.ic_order_ready),
    DELIVERY_DELAYED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_DELAYED.name, "Delayed"),
    DELIVERY_FAILED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.DELIVERY_FAILED.name, "Failed"),
    ORDER_COMPLETED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_COMPLETED.name, "Order Completed",R.drawable.ic_order_ready),
    ORDER_CANCELLED_1(OrderSummaryModel.OrderType.ORDER.name, OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name, "Cancelled",R.drawable.ic_order_canceled),
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
            (it.status.equals(status, ignoreCase = true) && it.type == OrderSummaryModel.OrderType.ORDER.name)
        }

        fun fromStatusAppointment(status: String): OrderStatusValue? = values().firstOrNull {
            (it.status.equals(status, ignoreCase = true) && it.type == OrderSummaryModel.OrderType.APPOINTMENT.name)
        }

        fun fromStatusConsultation(status: String): OrderStatusValue? = values().firstOrNull {
            (it.status.equals(status, ignoreCase = true) && it.type == OrderSummaryModel.OrderType.VIDEO_CONSULTATION.name)
        }

    }
}