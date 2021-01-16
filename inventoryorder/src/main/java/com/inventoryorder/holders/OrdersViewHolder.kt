package com.inventoryorder.holders

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

class OrdersViewHolder(binding: ItemOrderBinding) : AppBaseRecyclerViewHolder<ItemOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    binding.view.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE
    data?.let {
//            binding.orderDate.title.text = activity?.resources?.getString(R.string.date_order)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment_mode)
      binding.payment.icon.setImageResource(R.drawable.ic_payment_mode)

      binding.delivery.title.text = activity?.resources?.getString(R.string.delivery_mode_n)
      binding.delivery.icon.setImageResource(R.drawable.ic_delivery_mode)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener { listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal) }
  }

  private fun setDataResponse(order: OrderItem) {
    val orderStatusValue = OrderStatusValue.fromStatusOrder(order.status())
    val statusValue = orderStatusValue?.value
    val statusIcon = orderStatusValue?.icon

    if (statusIcon != null) binding.statusIcon.setImageResource(statusIcon) else binding.statusIcon.gone()

    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
        binding.orderType.text = OrderStatusValue.ABANDONED_1.value
      } else binding.orderType.text = statusValue.plus(order.cancelledText())

    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() }
          ?: "INR"
      val formatAmount = "${DecimalFormat("##,##,##0.00").format(BigDecimal(bill.AmountPayableByBuyer!!))}"
      val ss = SpannableString("$formatAmount")
      ss.setSpan(RelativeSizeSpan(0.5f), "$formatAmount".indexOf("."), "$formatAmount".length, 0)
      binding.txtRupeesSymble.text = currency
      binding.txtRupees.text = ss
    }

    binding.txtOrderDate.text = "${activity?.resources?.getString(R.string.at)} ${parseDate(order.UpdatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL, timeZone = TimeZone.getTimeZone("IST"))}"

    binding.delivery.value.text = order.deliveryType()
    val sizeItem = order.Items?.size ?: 0
//    binding.itemCount.text = "$sizeItem ${takeIf { sizeItem > 1 }?.let { "Items" } ?: "Item"}"
    binding.itemDesc.text = order.getTitles()

    binding.itemMore.visibility = takeIf { (sizeItem > 3) }?.let {
      binding.itemMore.text = "${sizeItem - 3} other items"
      View.VISIBLE
    } ?: View.GONE
    checkConfirmBtn(order)
    var colorCode = "#4a4a4a"
    OrderSummaryModel.OrderStatus.from(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderStatus.ORDER_INITIATED -> {
          //Order Initiated..
          colorCode = "#f16629"
//          changeButtonStatus(R.string.confirm_order_normal, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
//          binding.tvDropdownOrderStatus.setOnClickListener {
//            if (order.isConfirmActionBtn()) {
//              listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal)
//            }
//          }
        }
        OrderSummaryModel.OrderStatus.ORDER_CONFIRMED -> {
          //Order Confirmed
          colorCode = "#FFB900"
//          changeButtonStatus(R.string.mark_as_Ready_for_Pickup, R.drawable.ic_confirmed_order_btn_bkg, R.color.orange, R.drawable.ic_arrow_down_orange)
        }
        OrderSummaryModel.OrderStatus.PAYMENT_CONFIRMED -> {
          //Order Placed
          colorCode = "#f16629"
//          changeButtonStatus(R.string.confirm_order_normal, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderSummaryModel.OrderStatus.DELIVERY_IN_PROGRESS -> {
          //Order In-Transit
          colorCode = "#52AAC6"
//          changeButtonStatus(R.string.mark_as_delivered, R.drawable.ic_in_transit_order_btn_bkg, R.color.blue_52AAC6, R.drawable.ic_arrow_down_blue)
        }
        OrderSummaryModel.OrderStatus.ORDER_CANCELLED -> {
          //Order Cancelled
          colorCode = "#9B9B9B"
          changeButtonStatus(R.string.send_re_booking_reminder, R.drawable.ic_cancelled_order_btn_bkg, R.color.warm_grey_two, R.drawable.ic_arrow_down_grey)
        }
        OrderSummaryModel.OrderStatus.ORDER_COMPLETED -> {
          //Order Completed
          colorCode = "#f16629"
//          changeButtonStatus(R.string.send_payment_link, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        else -> {
          colorCode = "#f16629"
//          changeButtonStatus(R.string.confirm_order_normal, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
      }
    }
    binding.payment.value.text = fromHtml(order.PaymentDetails?.paymentWithColor(colorCode)?.trim() ?: "")
  }


  private fun changeButtonStatus(@StringRes dropDownText: Int, @DrawableRes buttonBkg: Int, @ColorRes dropDownDividerColor: Int, @DrawableRes resId: Int) {
    activity?.let {
      //Set Text...
      binding.tvDropdownOrderStatus.text = it.resources.getString(dropDownText)
      //Set Drawables and color...
      binding.tvDropdownOrderStatus.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.lytStatusBtn.background = ContextCompat.getDrawable(it, buttonBkg)
      binding.vwDividerDropdownOrderStatus.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.ivDropdownOrderStatus.setImageResource(resId)

      //DrawableCompat.setTint(binding.ivDropdownOrderStatus.drawable, ContextCompat.getColor(it.applicationContext, dropDownArrowColor))
    }
  }

  private fun checkConfirmBtn(order: OrderItem) {
    if (order.isConfirmActionBtn()) {
      binding.lytStatusBtn.visible()
      changeButtonStatus(R.string.confirm_order_normal, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
      binding.tvDropdownOrderStatus.setOnClickListener {
        if (order.isConfirmActionBtn()) {
          listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal)
        }
      }

    } else {
      binding.lytStatusBtn.gone()
    }
  }

//  private fun checkConfirmBtn(order: OrderItem) {
//    if (order.isConfirmActionBtn()) {
//      //binding.btnConfirm.visible()
//      //binding.btnConfirm.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_CONFIRM_CLICKED.ordinal) }
//    } else {
//      //binding.btnConfirm.gone()
//    }
//  }

  private fun buttonDisable(color: Int, border: Int) {
    activity?.let {
      //binding.btnConfirm.setTextColor(ContextCompat.getColor(it, color))
      //binding.btnConfirm.background = ContextCompat.getDrawable(it, border)
    }
  }
}