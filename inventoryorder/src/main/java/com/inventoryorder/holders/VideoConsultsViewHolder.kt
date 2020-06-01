package com.inventoryorder.holders

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemVideoConsultOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import java.util.*


class VideoConsultsViewHolder(binding: ItemVideoConsultOrderBinding) : AppBaseRecyclerViewHolder<ItemVideoConsultOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let {
      binding.bookingDate.title.text = activity?.resources?.getString(R.string.date_order)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment)
      binding.duration.title.text = activity?.resources?.getString(R.string.duration)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.VIDEO_CONSULT_ITEM_CLICKED.ordinal)
    }
  }

  private fun setDataResponse(order: OrderItem) {
    binding.orderType.text = OrderSummaryModel.OrderType.fromValue(order.status())?.type
    binding.bookingId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding.txtRupees.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding.bookingDate.value.text = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL)
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()
    binding.duration.value.text = order.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.durationTxt() ?: "0 Minute"
    val sizeItem = if (order.firstItemForConsultation() != null) 1 else 0
    binding.itemCount.text = takeIf { sizeItem > 1 }?.let { "Details" } ?: "Detail"
    binding.itemDesc.text = order.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.detailsConsultation() ?: ""

    binding.itemMore.visibility = takeIf { (sizeItem > 1) }?.let {
      binding.itemMore.text = "+${sizeItem - 1} more"
      View.VISIBLE
    } ?: View.GONE

    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val itemDate = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY) ?: ""

    OrderSummaryModel.OrderType.fromValue(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderType.RECEIVED, OrderSummaryModel.OrderType.PAYMENT_CONFIRM,
        OrderSummaryModel.OrderType.SUCCESSFUL,
        OrderSummaryModel.OrderType.ESCALATED,

        OrderSummaryModel.OrderType.ORDER_INITIATED,
        OrderSummaryModel.OrderType.PAYMENT_MODE_VERIFIED,
        OrderSummaryModel.OrderType.DELIVERY_IN_PROGRESS,
        OrderSummaryModel.OrderType.FEEDBACK_PENDING,
        OrderSummaryModel.OrderType.FEEDBACK_RECEIVED,
        OrderSummaryModel.OrderType.DELIVERY_DELAYED,
        OrderSummaryModel.OrderType.DELIVERY_FAILED,
        OrderSummaryModel.OrderType.DELIVERY_COMPLETED -> {
//          if (todayDate == itemDate) {
            checkPaymentConfirm(order)
            changeBackground(View.VISIBLE, View.GONE, R.drawable.new_order_bg, R.color.watermelon_light, R.color.light_green)
//          } else {
//            buttonVisibility(false)
//            backgroundGrey(View.VISIBLE, View.GONE, R.drawable.cancel_order_bg, R.color.primary_grey)
//          }
        }
        OrderSummaryModel.OrderType.ABANDONED,
        OrderSummaryModel.OrderType.CANCELLED -> {
          if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
            binding.orderType.text = OrderSummaryModel.OrderType.ABANDONED.type
          }
          binding.textErrorCall.gone()
          binding.btnCall.gone()
          changeBackground(View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey, R.color.primary_grey)
        }
      }
    }
    binding.itemMore.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG).let { binding.itemMore.paintFlags = it }
  }

  private fun checkPaymentConfirm(order: OrderItem) {
    if (order.isConfirmConsulting()) {
      buttonVisibility(true)
      binding.btnCall.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.VIDEO_CONSULT_CALL_CLICKED.ordinal) }
      binding.btnCopyLink.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.VIDEO_CONSULT_COPY_CLICKED.ordinal) }
      binding.btnCopyLink.visibility = View.VISIBLE
    } else {
      buttonVisibility(false)
      binding.btnCopyLink.visibility = View.GONE
    }
  }

  private fun buttonVisibility(isVisibleCall: Boolean) {
    binding.btnCall.visibility = if (isVisibleCall) View.VISIBLE else View.GONE
    binding.textErrorCall.visibility = if (!isVisibleCall) View.VISIBLE else View.GONE
  }

  private fun backgroundGrey(detail: Int, btn: Int, orderBg: Int, primaryGrey: Int) {
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    binding.btnCopyLink.visibility = View.GONE
    setColorView(primaryGrey, binding.bookingDate.title, binding.payment.title, binding.duration.title, binding.bookingId,
        binding.txtRupees, binding.bookingDate.value, binding.payment.value, binding.duration.value, binding.itemCount,
        binding.itemDesc, binding.itemMore)
    activity?.let { binding.orderType.background = ContextCompat.getDrawable(it, orderBg) }
  }

  private fun setColorView(color: Int, vararg view: CustomTextView) {
    activity?.let { ac -> view.forEach { it.setTextColor(ContextCompat.getColor(ac, color)) } }
  }

  private fun changeBackground(detail: Int, btn: Int, orderBg: Int, rupeesColor: Int, dateColor: Int) {
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    activity?.let {
      binding.bookingDate.value.setTextColor(ContextCompat.getColor(it, dateColor))
      binding.payment.value.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.duration.value.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.itemDesc.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }
  }

}