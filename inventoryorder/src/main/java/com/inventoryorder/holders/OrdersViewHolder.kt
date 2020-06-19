package com.inventoryorder.holders

import android.view.View
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.parseDate
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderStatusValue
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
    val statusValue = OrderStatusValue.fromStatusOrder(order.status())?.value
    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
        binding.orderType.text = OrderStatusValue.ESCALATED_1.value
      } else binding.orderType.text = statusValue.plus(order.cancelledText())
    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding.txtRupees.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding.orderDate.value.text = parseDate(order.UpdatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL, timeZone = TimeZone.getTimeZone("IST"))
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()
    binding.delivery.value.text = order.deliveryType()
    val sizeItem = order.Items?.size ?: 0
    binding.itemCount.text = "$sizeItem ${takeIf { sizeItem > 1 }?.let { "Items" } ?: "Item"}"
    binding.itemDesc.text = order.getTitles()

    binding.itemMore.visibility = takeIf { (sizeItem > 3) }?.let {
      binding.itemMore.text = "${sizeItem - 3} more"
      View.VISIBLE
    } ?: View.GONE
    OrderSummaryModel.OrderStatus.from(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderStatus.ORDER_CANCELLED -> {
          changeBackground(View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey)
          binding.btnConfirm.gone()
        }
        else -> {
          changeBackground(View.VISIBLE, View.GONE, R.drawable.new_order_bg, R.color.watermelon_light)
          checkConfirmBtn(order)
        }
      }
    }

  }

  private fun checkConfirmBtn(order: OrderItem) {
    if (order.isConfirmActionBtn()) {
      binding.btnConfirm.visible()
      binding.btnConfirm.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal) }
    } else binding.btnConfirm.gone()
  }

  private fun buttonDisable(color: Int, border: Int) {
    activity?.let {
      binding.btnConfirm.setTextColor(ContextCompat.getColor(it, color))
      binding.btnConfirm.background = ContextCompat.getDrawable(it, border)
    }
  }

  private fun changeBackground(detaile: Int, btn: Int, orderBg: Int, rupeesColor: Int) {
    binding.detailsOrder.visibility = detaile
    binding.next2.visibility = btn
    activity?.let {
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }
  }
}