package com.inventoryorder.holders

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemVideoConsultOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import java.util.*


class VideoConsultsViewHolder(binding: ItemVideoConsultOrderBinding) : AppBaseRecyclerViewHolder<ItemVideoConsultOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let {
      binding.consultDate.title.text = activity?.resources?.getString(R.string.date_consult)
      binding.createDate.title.text = activity?.resources?.getString(R.string.date_)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment_)
      binding.duration.title.text = activity?.resources?.getString(R.string.duration)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.VIDEO_CONSULT_ITEM_CLICKED.ordinal
      )
    }
  }

  private fun setDataResponse(order: OrderItem) {
    val statusValue = OrderStatusValue.fromStatusConsultation(order.status())?.value
    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
        binding.orderType.text = OrderStatusValue.ESCALATED_3.value
      } else binding.orderType.text = statusValue.plus(order.cancelledTextVideo())
    } else if (order.isConfirmConsultBtn()) binding.orderType.text = "Upcoming Consult"
    else binding.orderType.text = statusValue

    binding.bookingId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding.txtRupees.text = "$currency ${bill.GrossAmount}"
    }
    binding.createDate.value.text = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL, timeZone = TimeZone.getTimeZone("IST"))
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()
    binding.duration.value.text = order.firstItemForAptConsult()?.Product?.extraItemProductConsultation()?.durationTxt() ?: "0 Minute"
    val sizeItem = if (order.firstItemForAptConsult() != null) 1 else 0
    binding.itemCount.text = takeIf { sizeItem > 1 }?.let { "Details" } ?: "Detail"
    val details = order.firstItemForAptConsult()?.Product?.extraItemProductConsultation()?.detailsConsultation()
    binding.itemDesc.text = details

    val scheduleDate = order.firstItemForAptConsult()?.scheduledStartDate()
    val consultDate = parseDate(scheduleDate, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL_2)
    if (consultDate.isNullOrEmpty().not()) {
      binding.consultDate.mainView.visible()
      binding.consultDate.value.text = consultDate
    } else binding.consultDate.mainView.gone()

    binding.itemMore.visibility = takeIf { (sizeItem > 1) }?.let {
      binding.itemMore.text = "+${sizeItem - 1} more"
      View.VISIBLE
    } ?: View.GONE

//    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
//    val itemDate = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY) ?: ""

    OrderSummaryModel.OrderStatus.from(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderStatus.ORDER_CANCELLED -> {
          binding.textErrorCall.gone()
          binding.btnCall.gone()
          changeBackground(
            View.GONE,
            View.VISIBLE,
            R.drawable.cancel_order_bg,
            R.color.primary_grey,
            R.color.primary_grey
          )
        }
        else -> {
          checkConfirmBtn(order)
          changeBackground(
            View.VISIBLE,
            View.GONE,
            R.drawable.ic_apt_order_bg,
            R.color.watermelon_light,
            R.color.light_green
          )
        }
      }
    }
    binding.itemMore.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG)
      .let { binding.itemMore.paintFlags = it }
  }

  private fun checkConfirmBtn(order: OrderItem) {
    if (order.isConfirmConsultBtn()) {
      binding.btnCall.visible()
      binding.btnCall.setOnClickListener {
        listener?.onItemClick(
          adapterPosition,
          order,
          RecyclerViewActionType.VIDEO_CONSULT_CALL_CLICKED.ordinal
        )
      }
      binding.btnCopyLink.setOnClickListener {
        listener?.onItemClick(
          adapterPosition,
          order,
          RecyclerViewActionType.VIDEO_CONSULT_COPY_CLICKED.ordinal
        )
      }
//      binding.btnCopyLink.visibility = View.VISIBLE
      binding.textErrorCall.gone()
    } else {
      binding.btnCall.gone()
//      binding.btnCopyLink.visibility = View.GONE
      binding.textErrorCall.visibility =
        if (!order.isConsultErrorText()) View.VISIBLE else View.GONE
    }
  }

  private fun backgroundGrey(detail: Int, btn: Int, orderBg: Int, primaryGrey: Int) {
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
//    binding.btnCopyLink.visibility = View.GONE
    setColorView(
      primaryGrey,
      binding.consultDate.title,
      binding.payment.title,
      binding.duration.title,
      binding.bookingId,
      binding.txtRupees,
      binding.consultDate.value,
      binding.payment.value,
      binding.duration.value,
      binding.itemCount,
      binding.itemDesc,
      binding.itemMore
    )
    activity?.let { binding.orderType.background = ContextCompat.getDrawable(it, orderBg) }
  }

  private fun setColorView(color: Int, vararg view: CustomTextView) {
    activity?.let { ac -> view.forEach { it.setTextColor(ContextCompat.getColor(ac, color)) } }
  }

  private fun changeBackground(
    detail: Int,
    btn: Int,
    orderBg: Int,
    rupeesColor: Int,
    dateColor: Int
  ) {
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    activity?.let {
      binding.consultDate.value.setTextColor(ContextCompat.getColor(it, dateColor))
      binding.createDate.value.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.payment.value.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.duration.value.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.itemDesc.setTextColor(ContextCompat.getColor(it, R.color.greyish_brown))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }
  }

}