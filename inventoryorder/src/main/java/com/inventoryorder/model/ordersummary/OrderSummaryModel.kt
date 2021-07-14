package com.inventoryorder.model.ordersummary

import androidx.annotation.ColorRes
import com.framework.base.BaseResponse
import com.framework.utils.*
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

const val TOTAL_SELLER_SUMMARY = "TOTAL_SELLER_SUMMARY"
const val SELLER_BUSINESS_REPORT = "SELLER_BUSINESS_REPORT"
const val TOTAL_SELLER_ENQUIRIES = "TOTAL_SELLER_ENQUIRIES"

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
  val FeedbackSummary: FeedbackSummary? = null,

  val type: String? = null,
  val count: Int? = null,
  @ColorRes val color: Int? = null,
  var isSelected: Boolean = false,
) : BaseResponse(), Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ORDERS_ITEM_TYPE.getLayout()
  }

  fun getTotalNetAmount(): String {
    return getNumberFormat((TotalNetAmount ?: 0).toInt().toString())
  }

  fun getTotalOrders(): String {
    return getNumberFormat((TotalOrders ?: 0).toInt().toString())
  }

  fun getOrderType(): ArrayList<OrderSummaryModel> {
    val list = ArrayList<OrderSummaryModel>()
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.TOTAL.type,
        count = TotalOrders,
        isSelected = true,
        color = R.color.orange
      )
    )
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.RECEIVED.type,
        count = TotalOrdersInProgress,
        color = R.color.watermelon_light
      )
    )
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.SUCCESSFUL.type,
        count = TotalOrdersCompleted,
        color = R.color.green_27AE60
      )
    )
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.CANCELLED.type,
        count = TotalOrdersCancelled,
        color = R.color.pinkish_grey
      )
    )
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.ABANDONED.type,
        count = TotalOrdersAbandoned,
        color = R.color.red_F40000
      )
    )
    list.add(
      OrderSummaryModel(
        type = OrderSummaryType.ESCALATED.type,
        count = TotalOrdersEscalated,
        color = R.color.red_F40000
      )
    )
    return list
  }

  enum class OrderSummaryType(val type: String, val value: String) {
    TOTAL("All", ""),
    RECEIVED("Received", OrderStatus.ORDER_CONFIRMED.name),
    PAYMENT_CONFIRM("Payment confirmed", OrderStatus.PAYMENT_CONFIRMED.name),


    SUCCESSFUL("Completed", OrderStatus.ORDER_COMPLETED.name),

    ESCALATED("Escalated", OrderStatus.ESCALATED.name),

    CANCELLED("Cancelled", OrderStatus.ORDER_CANCELLED.name),/* with Payment Status != CANCELLED */
    ABANDONED("Abandoned", OrderStatus.ORDER_CANCELLED.name), /* with Payment Status == CANCELLED */

    ORDER_INITIATED("Initiated", OrderStatus.ORDER_INITIATED.name),
    PAYMENT_MODE_VERIFIED("Payment verified", OrderStatus.PAYMENT_MODE_VERIFIED.name),
    ORDER_CONFIRMED("Order confirm", OrderStatus.ORDER_CONFIRMED.name),
    DELIVERY_IN_PROGRESS("Delivery in-progress", OrderStatus.DELIVERY_IN_PROGRESS.name),
    FEEDBACK_PENDING("Feedback pending", OrderStatus.FEEDBACK_PENDING.name),
    FEEDBACK_RECEIVED("Feedback received", OrderStatus.FEEDBACK_RECEIVED.name),
    DELIVERY_DELAYED("Delivery delayed", OrderStatus.DELIVERY_DELAYED.name),
    DELIVERY_FAILED("Delivery failed", OrderStatus.DELIVERY_FAILED.name),
    DELIVERY_COMPLETED("Delivery completed", OrderStatus.DELIVERY_COMPLETED.name);


    companion object {
      fun fromType(type: String): OrderSummaryType? =
        values().firstOrNull { it.type.equals(type, ignoreCase = true) }

      fun fromValue(value: String): OrderSummaryType? =
        values().firstOrNull { it.value.equals(value, ignoreCase = true) }
    }
  }

  enum class OrderStatus {
    ORDER_INITIATED, PAYMENT_MODE_VERIFIED, PAYMENT_CONFIRMED, ORDER_CONFIRMED, DELIVERY_IN_PROGRESS, DELIVERY_COMPLETED,
    FEEDBACK_PENDING, FEEDBACK_RECEIVED, DELIVERY_DELAYED, DELIVERY_FAILED, ORDER_COMPLETED, ORDER_CANCELLED, ESCALATED;

    companion object {
      fun from(value: String?): OrderStatus? =
        values().firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
  }

  enum class OrderType {
    ORDER, APPOINTMENT, VIDEO_CONSULTATION;
  }

  fun getSellerSummary(key: String): OrderSummaryModel? {
    val resp = PreferencesUtils.instance.getData(key, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData(key: String) {
    PreferencesUtils.instance.saveData(key, convertObjToString(this) ?: "")
  }

  fun getTotalOrder(key: String): String? {
    return PreferencesUtils.instance.getData(key, "")
  }

  fun saveTotalOrder(key: String) {
    PreferencesUtils.instance.saveData(key, getTotalOrders())
  }
}

class FeedbackSummary(
  val AverageFeedback: Double? = null,
  val TotalFeedbacksReceived: Int? = null,
)

