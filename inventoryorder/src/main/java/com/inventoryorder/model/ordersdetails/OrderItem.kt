package com.inventoryorder.model.ordersdetails

import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersummary.OrderMenuModel
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
    val _id: String? = null,
    val PlacedOrderStatus: Int? = null,
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
    return parseDate(CreatedOn, FORMAT_SERVER_DATE, format, timeZone = TimeZone.getTimeZone("IST"))?.parseDate(DateUtils.FORMAT_DD_MM_YYYY)
  }

  fun getInvoiceUrl(): String {
    return BillingDetails?.InvoiceUrl?.trim() ?: ""
  }

  fun referenceNumber(): String {
    return ReferenceNumber?.trim()?.toLowerCase(Locale.ROOT) ?: ""
  }

  fun deliveryType(): String? {
    return when (Mode?.toUpperCase(Locale.ROOT)) {
      OrderSummaryRequest.OrderMode.DELIVERY.name -> "Home delivery"
      OrderSummaryRequest.OrderMode.PICKUP.name -> "Self delivery"
      else -> Mode?.capitalizeUtil()
    }
  }

  fun cancelledText(): String {
    return if (CancellationDetails != null) {
      when (CancellationDetails.cancelledBy().trim().toUpperCase(Locale.ROOT)) {
        CancellingEntity.SELLER.name -> return " You"
        CancellingEntity.BUYER.name, CancellingEntity.NF.name -> return " Customer"
        else -> " ${CancellationDetails.cancelledBy()}"
      }
    } else ""
  }

  fun cancelledTextVideo(): String {
    return if (CancellationDetails != null) {
      when (CancellationDetails.cancelledBy().trim().toUpperCase(Locale.ROOT)) {
        CancellingEntity.SELLER.name -> return " You"
        CancellingEntity.BUYER.name, CancellingEntity.NF.name -> return " Patient"
        else -> " ${CancellationDetails.cancelledBy()}"
      }
    } else ""
  }

  fun getTitles(): String {
    var title = ""
    Items?.forEachIndexed { index, item ->
      if (index < 3) title += takeIf { index != 0 }?.let { "\n●  ${item.Quantity} x  ${item.Product?.Name?.trim()}" } ?: ("●  ${item.Quantity} x  ${item.Product?.Name?.trim()}")
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

  enum class OrderMode {
    DELIVERY, PICKUP, APPOINTMENT
  }

  enum class DeliveryMode {
    ONLINE, OFFLINE
  }

  enum class CancellingEntity {
    SELLER, BUYER, NF;

    companion object {
      fun from(value: String): CancellingEntity? = values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == value.toLowerCase(Locale.ROOT) }
    }
  }

  fun consultationWindowUrlForPatient(): String {
    return "https://p.nflo.at/consult?appt=$_id"
  }

  fun consultationWindowUrlForDoctor(): String {
    return "https://d.nflo.at/consult?appt=$_id"
  }

  fun consultationJoiningUrl(webSiteUrl: String?): String {
    return "https://$webSiteUrl/consult/$_id"
  }

  fun isConfirmConsultBtn(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_INITIATED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED)) && isUpComingConsult()
  }

  fun isRescheduleConsultBen(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_INITIATED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED))
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
        || (PaymentDetailsN.METHOD.fromType(PaymentDetails.method()) == PaymentDetailsN.METHOD.COD)
        || (PaymentDetailsN.METHOD.fromType(PaymentDetails.method()) == PaymentDetailsN.METHOD.FREE)))
  }

  fun isCancelActionBtn(): Boolean {
    return ((OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED ||
        OrderSummaryModel.OrderStatus.from(status()) == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        LogisticsDetails != null && LogisticsDetailsN.LogisticsStatus.from(LogisticsDetails.status()) == LogisticsDetailsN.LogisticsStatus.NOT_INITIATED)
  }

  fun orderBtnStatus(): ArrayList<OrderMenuModel.MenuStatus> {
    val statusOrder = OrderSummaryModel.OrderStatus.from(status())
    val method = PaymentDetailsN.METHOD.fromType(PaymentDetails?.method())
    val statusPayment = PaymentDetailsN.STATUS.from(PaymentDetails?.status())

    if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_ORDER, OrderMenuModel.MenuStatus.CANCEL_ORDER)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        (method != PaymentDetailsN.METHOD.FREE || (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_ORDER)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CONFIRM_ORDER, OrderMenuModel.MenuStatus.CANCEL_ORDER, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) && (method != PaymentDetailsN.METHOD.FREE ||
            (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_ORDER, OrderMenuModel.MenuStatus.CANCEL_ORDER)
      else arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_ORDER, OrderMenuModel.MenuStatus.CANCEL_ORDER, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING
        && (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING && method != PaymentDetailsN.METHOD.FREE &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED || statusOrder == OrderSummaryModel.OrderStatus.ORDER_COMPLETED) &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED || statusOrder == OrderSummaryModel.OrderStatus.ORDER_COMPLETED) &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf()
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.PENDING ||
            statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_DELIVERED)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_DELIVERED, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_DELIVERED)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        (method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_DELIVERED)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_DELIVERED, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_ORDER)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && (method == PaymentDetailsN.METHOD.FREE ||
            (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.CANCEL_ORDER)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.CANCEL_ORDER)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE, OrderMenuModel.MenuStatus.CANCEL_ORDER)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ESCALATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ESCALATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf()
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_SHIPPED, OrderMenuModel.MenuStatus.CANCEL_ORDER)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_SHIPPED, OrderMenuModel.MenuStatus.CANCEL_ORDER, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && (method == PaymentDetailsN.METHOD.FREE ||
            (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SHIPPED, OrderMenuModel.MenuStatus.CANCEL_ORDER)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SHIPPED, OrderMenuModel.MenuStatus.CANCEL_ORDER)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SHIPPED, OrderMenuModel.MenuStatus.CANCEL_ORDER, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CANCELLED) {

      return arrayListOf(OrderMenuModel.MenuStatus.SEND_RE_BOOKING)

    }
    return arrayListOf()
  }

  fun appointmentButtonStatus(): ArrayList<OrderMenuModel.MenuStatus> {
    return appointmentSpaButtonStatus()
  }

  fun appointmentSpaButtonStatus(): ArrayList<OrderMenuModel.MenuStatus> {
    val statusOrder = OrderSummaryModel.OrderStatus.from(status())
    val method = PaymentDetailsN.METHOD.fromType(PaymentDetails?.method())
    val statusPayment = PaymentDetailsN.STATUS.from(PaymentDetails?.status())

    if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) &&
        (method != PaymentDetailsN.METHOD.FREE || (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_MODE_VERIFIED || statusOrder == OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED) && (method != PaymentDetailsN.METHOD.FREE ||
            (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING && (method == PaymentDetailsN.METHOD.FREE ||
            (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_PENDING && method != PaymentDetailsN.METHOD.FREE &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE, OrderMenuModel.MenuStatus.REQUEST_FEEDBACK)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED || statusOrder == OrderSummaryModel.OrderStatus.ORDER_COMPLETED) &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED || statusOrder == OrderSummaryModel.OrderStatus.ORDER_COMPLETED)
        && method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf()
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.FEEDBACK_RECEIVED || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_COMPLETED || statusOrder == OrderSummaryModel.OrderStatus.ORDER_COMPLETED)
        && (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf()

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED ||
            statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_SERVED)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_AS_SERVED, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        (method == PaymentDetailsN.METHOD.FREE || (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SERVED)

    } else if ((statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS || statusOrder == OrderSummaryModel.OrderStatus.DELIVERY_DELAYED) &&
        (method != PaymentDetailsN.METHOD.FREE && (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS))) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SERVED)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_AS_SERVED, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && (method == PaymentDetailsN.METHOD.FREE ||
            (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_INITIATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ESCALATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED ||
            statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ESCALATED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf()
      else arrayListOf(OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.PENDING || statusPayment == PaymentDetailsN.STATUS.FAILED || statusPayment == PaymentDetailsN.STATUS.CANCELLED)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.START_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.REQUEST_PAYMENT, OrderMenuModel.MenuStatus.START_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && (method == PaymentDetailsN.METHOD.FREE ||
            (method != PaymentDetailsN.METHOD.FREE && statusPayment == PaymentDetailsN.STATUS.SUCCESS))) {

      return arrayListOf(OrderMenuModel.MenuStatus.START_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CONFIRMED && method != PaymentDetailsN.METHOD.FREE &&
        (statusPayment == PaymentDetailsN.STATUS.INITIATED || statusPayment == PaymentDetailsN.STATUS.INPROCESS)) {

      return if (method != PaymentDetailsN.METHOD.COD) arrayListOf(OrderMenuModel.MenuStatus.START_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT)
      else arrayListOf(OrderMenuModel.MenuStatus.START_APPOINTMENT, OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT, OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE)

    } else if (statusOrder == OrderSummaryModel.OrderStatus.ORDER_CANCELLED) {

      return arrayListOf(OrderMenuModel.MenuStatus.SEND_RE_BOOKING)

    }
    return arrayListOf()
  }


  fun firstItemForAptConsult(): ItemN? {
    return Items?.firstOrNull()
  }


  fun isUpComingConsult(): Boolean {
    val date = firstItemForAptConsult()?.scheduledEndDate()?.parseDate(FORMAT_SERVER_DATE)
    return date?.after(getCurrentDate()) ?: false
  }
}
