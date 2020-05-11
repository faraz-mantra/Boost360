package com.inventoryorder.holders

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBookingsAllOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem


class BookingsViewHolder(binding: ItemBookingsAllOrderBinding) : AppBaseRecyclerViewHolder<ItemBookingsAllOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let {
      binding.bookingDate.title.text = activity?.resources?.getString(R.string.date_order)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment)
      binding.location.title.text = activity?.resources?.getString(R.string.location)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
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
    binding.payment.value.text = order.PaymentDetails?.Method?.trim()
    binding.location.value.text = order.SellerDetails?.Address?.City?.capitalize()
    val sizeItem = order.Items?.size ?: 0
    binding.itemCount.text = takeIf { sizeItem > 1 }?.let { "Services" } ?: "Service"
    binding.itemDesc.text = order.getTitlesBooking()

    binding.itemMore.visibility = takeIf { (sizeItem > 1) }?.let {
      binding.itemMore.text = "+${sizeItem - 1} more"
      View.VISIBLE
    } ?: View.GONE

    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val itemDate = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY) ?: ""

    OrderSummaryModel.OrderType.fromValue(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderType.RECEIVED,
        OrderSummaryModel.OrderType.SUCCESSFUL,
        OrderSummaryModel.OrderType.RETURNED,
        OrderSummaryModel.OrderType.ABANDONED,
        OrderSummaryModel.OrderType.ESCALATED -> {
          if (todayDate == itemDate) {
            changeBackground(View.VISIBLE, View.VISIBLE, View.GONE, R.drawable.new_order_bg, R.color.watermelon_light, R.color.light_green)
          } else backgroundGrey(View.VISIBLE, View.VISIBLE, View.GONE, R.drawable.cancel_order_bg, R.color.primary_grey)
        }
        OrderSummaryModel.OrderType.CANCELLED -> {
          changeBackground(View.GONE, View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey, R.color.primary_grey)
        }
      }
    }
    binding.itemMore.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG).let { binding.itemMore.paintFlags = it }
  }

  private fun backgroundGrey(confirm: Int, detail: Int, btn: Int, orderBg: Int, primaryGrey: Int) {
    binding.btnConfirm.visibility = confirm
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    binding.elapsedView.visibility = View.GONE
    setColorView(primaryGrey, binding.bookingDate.title, binding.payment.title, binding.location.title, binding.bookingId,
        binding.txtRupees, binding.bookingDate.value, binding.payment.value, binding.location.value, binding.itemCount,
        binding.itemDesc, binding.itemMore)
    activity?.let {
      binding.btnConfirm.setTextColor(ContextCompat.getColor(it, primaryGrey))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.btnConfirm.background = ContextCompat.getDrawable(it, R.drawable.btn_rounded_grey_border)
    }
    binding.btnConfirm.paintFlags = binding.btnConfirm.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
  }

  private fun setColorView(color: Int, vararg view: CustomTextView) {
    activity?.let { ac -> view.forEach { it.setTextColor(ContextCompat.getColor(ac, color)) } }
  }

  private fun changeBackground(confirm: Int, detail: Int, btn: Int, orderBg: Int, rupeesColor: Int, dateColor: Int) {
    binding.btnConfirm.visibility = confirm
    binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    activity?.let {
      binding.bookingDate.value.setTextColor(ContextCompat.getColor(it, dateColor))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }
  }

}