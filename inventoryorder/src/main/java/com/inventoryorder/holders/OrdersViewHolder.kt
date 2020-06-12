package com.inventoryorder.holders

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import java.util.*

class OrdersViewHolder(binding: ItemOrderBinding) : AppBaseRecyclerViewHolder<ItemOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    binding.view.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE
    data?.let {
      binding.orderDate.title.text = activity?.resources?.getString(R.string.date_order)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment)
      binding.delivery.title.text = activity?.resources?.getString(R.string.delivery)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal)
    }
  }

  private fun setDataResponse(order: OrderItem) {
    checkPaymentConfirm(order)
    binding.orderType.text = OrderSummaryModel.OrderSummaryType.fromValue(order.status())?.type
    binding.orderId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding.txtRupees.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding.orderDate.value.text = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL)
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()
    binding.delivery.value.text = order.LogisticsDetails?.DeliveryMode?.trim()
    val sizeItem = order.Items?.size ?: 0
    binding.itemCount.text = "$sizeItem ${takeIf { sizeItem > 1 }?.let { "Items" } ?: "Item"}"
    binding.itemDesc.text = order.getTitles()

    binding.itemMore.visibility = takeIf { (sizeItem > 3) }?.let {
      binding.itemMore.text = "${sizeItem - 3} more"
      View.VISIBLE
    } ?: View.GONE
    OrderSummaryModel.OrderSummaryType.fromValue(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderSummaryType.RECEIVED, OrderSummaryModel.OrderSummaryType.PAYMENT_CONFIRM,
        OrderSummaryModel.OrderSummaryType.SUCCESSFUL,
        OrderSummaryModel.OrderSummaryType.ESCALATED,

        OrderSummaryModel.OrderSummaryType.ORDER_INITIATED,
        OrderSummaryModel.OrderSummaryType.PAYMENT_MODE_VERIFIED,
        OrderSummaryModel.OrderSummaryType.DELIVERY_IN_PROGRESS,
        OrderSummaryModel.OrderSummaryType.FEEDBACK_PENDING,
        OrderSummaryModel.OrderSummaryType.FEEDBACK_RECEIVED,
        OrderSummaryModel.OrderSummaryType.DELIVERY_DELAYED,
        OrderSummaryModel.OrderSummaryType.DELIVERY_FAILED,
        OrderSummaryModel.OrderSummaryType.DELIVERY_COMPLETED -> {
          changeBackground(View.VISIBLE, View.VISIBLE, View.GONE, R.drawable.new_order_bg, R.color.watermelon_light)
        }
        OrderSummaryModel.OrderSummaryType.ABANDONED,
        OrderSummaryModel.OrderSummaryType.CANCELLED -> {
          if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
            binding.orderType.text = OrderSummaryModel.OrderSummaryType.ABANDONED.type
          }
          changeBackground(View.GONE, View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey)
        }
      }
    }

  }

  private fun checkPaymentConfirm(order: OrderItem) {
    if (order.isConfirmActionBtn()) {
      buttonDisable(R.color.colorAccent, R.drawable.btn_rounded_orange_border)
      binding.btnConfirm.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal) }
      binding.btnConfirm.paintFlags = 0
    } else {
      buttonDisable(R.color.primary_grey, R.drawable.btn_rounded_grey_border)
      binding.btnConfirm.paintFlags = binding.btnConfirm.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
  }

  private fun buttonDisable(color: Int, border: Int) {
    activity?.let {
      binding.btnConfirm.setTextColor(ContextCompat.getColor(it, color))
      binding.btnConfirm.background = ContextCompat.getDrawable(it, border)
    }
  }

  private fun changeBackground(confirm: Int, detaile: Int, btn: Int, orderBg: Int, rupeesColor: Int) {
    binding.btnConfirm.visibility = confirm
    binding.detailsOrder.visibility = detaile
    binding.next2.visibility = btn
    activity?.let {
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }
  }
}

//          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//           binding.timeElapsed.compoundDrawableTintList = ContextCompat.getColorStateList(it, R.color.warm_grey_10)
//          }