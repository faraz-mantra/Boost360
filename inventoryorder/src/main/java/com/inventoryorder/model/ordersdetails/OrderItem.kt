package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class OrderItem(
    val BillingDetails: BillingDetailsN? = null,
    val BuyerDetails: BuyerDetailsN? = null,
    val CancellationDetails: CancellationDetailsN? = null,
    var CreatedOn: String? = null,
    val InventoryDetails: ArrayList<InventoryDetailN>? = null,
    val IsArchived: Boolean? = null,
    val Items: ArrayList<ItemN>? = null,
    val LogisticsDetails: LogisticsDetailsN? = null,
    val Mode: String? = null,
    val OrderAmountMatch: Boolean? = null,
    val PaymentDetails: PaymentDetailsN? = null,
    val ReferenceNumber: String? = null,
    val RefundDetails: RefundDetailsN? = null,
    val SellerDetails: SellerDetailsN? = null,
    val SettlementDetails: SettlementDetailsN? = null,
    var Status: String? = null,
    val UpdatedOn: String? = null,
    val _id: String? = null
) : AppBaseRecyclerViewItem, Serializable {

  var dateKey: Date? = null
  var recyclerViewType = RecyclerViewItemType.INVENTORY_ORDER_ITEM.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }

  fun status(): String {
    return Status?.toLowerCase() ?: ""
  }

  fun stringToDate(format: String = DateUtils.FORMAT_DD_MM_YYYY): Date? {
    return parseDate(CreatedOn, DateUtils.FORMAT_SERVER_DATE, format)?.parseDate(DateUtils.FORMAT_DD_MM_YYYY)
  }

  fun referenceNumber(): String {
    return ReferenceNumber?.trim()?.toLowerCase() ?: ""
  }

  fun getTitles(): String {
    var title = ""
    Items?.forEachIndexed { index, item ->
      if (index < 3) title += takeIf { index != 0 }?.let { "\n${item.Product?.Name?.trim()}" } ?: (item.Product?.Name?.trim())
    }
    return title
  }

  fun getTitlesBooking(): String {
    var title = ""
    Items?.forEachIndexed { index, item ->
      if (index < 1) title += takeIf { index != 0 }?.let { "\n${item.Product?.Name?.trim()}" } ?: (item.Product?.Name?.trim())
    }
    return title
  }

  fun getLoaderItem(): OrderItem {
    this.recyclerViewType = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
    return this
  }


  fun getDateObject(date: Date): OrderItem {
    val item = OrderItem()
    item.dateKey = date
    item.recyclerViewType = RecyclerViewItemType.BOOKINGS_DATE_TYPE.getLayout()
    return item
  }

  enum class CancellingEntity {
    SELLER, BUYER, NF;

    companion object {
      fun from(value: String): CancellingEntity? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  fun isConfirmBooking(): Boolean {
    return (OrderSummaryModel.OrderType.fromValue(status()) == OrderSummaryModel.OrderType.PAYMENT_CONFIRM &&
        PaymentDetails != null && PaymentDetailsN.METHOD.from(PaymentDetails.method()) == PaymentDetailsN.METHOD.ONLINEPAYMENT &&
        PaymentDetailsN.STATUS.from(PaymentDetails.status()) == PaymentDetailsN.STATUS.SUCCESS)
  }

  fun isCancelBooking(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_INITIATED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        LogisticsDetails != null && LogisticsDetailsN.STSTUS.from(LogisticsDetails.status()) == LogisticsDetailsN.STSTUS.NOT_INITIATED)
    //PaymentDetails != null && PaymentDetailsN.STSTUS.from(PaymentDetails.status()) == PaymentDetailsN.STSTUS.SUCCESS
  }
}
