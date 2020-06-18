package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import com.inventoryorder.utils.capitalizeUtil
import java.io.Serializable
import java.util.*

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
    return Status ?: ""
  }

  fun stringToDate(format: String = DateUtils.FORMAT_DD_MM_YYYY): Date? {
    return parseDate(CreatedOn, DateUtils.FORMAT_SERVER_DATE, format)?.parseDate(DateUtils.FORMAT_DD_MM_YYYY)
  }

  fun referenceNumber(): String {
    return ReferenceNumber?.trim()?.toLowerCase() ?: ""
  }

  fun deliveryType(): String? {
    return when (Mode?.toUpperCase(Locale.ROOT)) {
      OrderSummaryRequest.OrderMode.DELIVERY.name -> "Assured Purchase"
      OrderSummaryRequest.OrderMode.PICKUP.name -> "Self Delivery"
      else -> Mode?.capitalizeUtil()
    }
  }

  fun cancelledText(): String {
    return if (CancellationDetails != null) {
      takeIf { CancellationDetails.cancelledBy().toUpperCase(Locale.ROOT) == CancellingEntity.SELLER.name }?.let { " You" } ?: " " + CancellationDetails.cancelledBy()
    } else ""
  }

  fun cancelledTextVideo(): String {
    val str = CancellationDetails?.cancelledBy()?.trim()?.toUpperCase(Locale.ROOT) ?: ""
    return if (str == CancellingEntity.BUYER.name || str == CancellingEntity.NF.name) " Patient"
    else cancelledText()
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
    item.recyclerViewType = RecyclerViewItemType.DATE_VIEW_TYPE.getLayout()
    return item
  }

  enum class CancellingEntity {
    SELLER, BUYER, NF;

    companion object {
      fun from(value: String): CancellingEntity? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  fun consultationWindowUrl(): String {
    return "http://d.nflo.at/consult?appt=$_id"
  }

  fun consultationJoiningUrl(): String {
    return "http://DOCTORS.GETBOOST360.COM/consult/$_id"
  }

  fun isConfirmConsultBtn(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_INITIATED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED) &&
        (firstItemForConsultation()?.Product?.isAvailable() ?: false) && isUpComingConsult())
  }

  fun isConsultErrorText(): Boolean {
    return (OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_COMPLETED)
  }

  fun isConfirmActionBtn(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED
        || OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        PaymentDetails != null && ((PaymentDetailsN.METHOD.fromType(PaymentDetails.method()) == PaymentDetailsN.METHOD.ONLINEPAYMENT &&
        PaymentDetailsN.STATUS.from(PaymentDetails.status()) == PaymentDetailsN.STATUS.SUCCESS)
        || (PaymentDetailsN.METHOD.fromType(PaymentDetails.method()) == PaymentDetailsN.METHOD.COD)))
  }

  fun isCancelActionBtn(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        LogisticsDetails != null && LogisticsDetailsN.STSTUS.from(LogisticsDetails.status()) == LogisticsDetailsN.STSTUS.NOT_INITIATED)
  }

  fun firstItemForConsultation(): ItemN? {
    return Items?.firstOrNull()
  }


  fun isUpComingConsult(): Boolean {
    val date = firstItemForConsultation()?.scheduledEndDate()?.parseDate(FORMAT_SERVER_DATE)
    return date?.after(getCurrentDate()) ?: false
  }
}
