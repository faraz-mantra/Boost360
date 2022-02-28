package com.inventoryorder.holders

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
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
import com.inventoryorder.model.ordersummary.OrderMenuModel
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
      binding.payment.title.text = activity?.resources?.getString(R.string.payment_mode)
      binding.payment.icon.setImageResource(R.drawable.ic_payment_mode)

      binding.delivery.title.text = activity?.resources?.getString(R.string.delivery_mode_n)
      binding.delivery.icon.setImageResource(R.drawable.ic_delivery_mode)
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.ORDER_ITEM_CLICKED.ordinal
      )
    }
  }

  private fun setDataResponse(order: OrderItem) {
    val orderStatusValue = OrderStatusValue.fromStatusOrder(order.status())
    val statusValue = orderStatusValue?.value
    val statusIcon = orderStatusValue?.icon

    if (statusIcon != null) binding.statusIcon.setImageResource(statusIcon) else binding.statusIcon.gone()

    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status()
        .toUpperCase(Locale.ROOT)
    ) {
      binding.orderType.text = statusValue.plus(order.cancelledText())
    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"
    order.BillingDetails?.let { bill ->
      val currency =
        takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() }
          ?: "INR"
      val formatAmount =
        "${DecimalFormat("##,##,##0.00").format(BigDecimal(bill.GrossAmount!!))}"
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
    var colorCode = "#9B9B9B"
    activity?.let {
      binding.statusView.background = ContextCompat.getDrawable(it, R.drawable.ic_new_order_bg)
    }
    val btnStatusMenu = order.orderBtnStatus()
    binding.lytStatusBtn.visible()
    if (btnStatusMenu.isNullOrEmpty().not()) {
      when (val btnOrderMenu = btnStatusMenu.removeAt(0)) {
        OrderMenuModel.MenuStatus.CONFIRM_ORDER -> {
          colorCode = "#f16629"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_initiated_order_btn_bkg,
            R.color.white,
            R.drawable.ic_arrow_down_white
          )
        }
        OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_initiated_order_btn_bkg,
            R.color.white,
            R.drawable.ic_arrow_down_white
          )
        }
        OrderMenuModel.MenuStatus.CANCEL_ORDER -> {
          colorCode = "#9B9B9B"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_cancelled_order_btn_bkg,
            R.color.warm_grey_two,
            R.drawable.ic_arrow_down_grey
          )
        }
        OrderMenuModel.MenuStatus.SEND_RE_BOOKING -> {
          colorCode = "#9B9B9B"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_cancelled_order_btn_bkg,
            R.color.warm_grey_two,
            R.drawable.ic_arrow_down_grey
          )
        }
        OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> {
          colorCode = "#FFB900"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_confirmed_order_btn_bkg,
            R.color.orange,
            R.drawable.ic_arrow_down_orange
          )
        }
        OrderMenuModel.MenuStatus.MARK_AS_DELIVERED -> {
          colorCode = "#78AF00"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_transit_order_btn_green,
            R.color.green_78AF00,
            R.drawable.ic_arrow_down_green
          )
        }
        OrderMenuModel.MenuStatus.MARK_AS_SHIPPED -> {
          colorCode = "#f16629"
          activity?.let {
            binding.statusView.background =
              ContextCompat.getDrawable(it, R.drawable.ic_new_order_bg_green)
          }
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_initiated_order_btn_green,
            R.color.white,
            R.drawable.ic_arrow_down_white
          )
        }
        OrderMenuModel.MenuStatus.REQUEST_FEEDBACK -> {
          colorCode = "#4A4A4A"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_in_transit_order_btn_bkg,
            R.color.black_4a4a4a,
            R.drawable.ic_arrow_down_4a4a4a
          )
        }
        else -> binding.lytStatusBtn.gone()
      }
      binding.tvDropdownOrderStatus.setOnClickListener {
        listener?.onItemClick(
          adapterPosition,
          order,
          RecyclerViewActionType.ORDER_BUTTON_CLICKED.ordinal
        )
      }
    } else binding.lytStatusBtn.gone()

    if (btnStatusMenu.isNullOrEmpty()) {
      binding.divider.gone()
      binding.ivDropdown.gone()
    } else {
      binding.ivDropdown.setOnClickListener {
        listener?.onItemClickView(
          adapterPosition,
          it,
          order,
          RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal
        )
      }
      binding.divider.visible()
      binding.ivDropdown.visible()
    }
    binding.payment.value.text =
      fromHtml(order.PaymentDetails?.paymentWithColor(colorCode)?.trim() ?: "")
  }


  private fun changeButtonStatus(
    btnTitle: String,
    @DrawableRes buttonBkg: Int,
    @ColorRes dropDownDividerColor: Int,
    @DrawableRes resId: Int
  ) {
    activity?.let {
      binding.tvDropdownOrderStatus.text = btnTitle
      binding.tvDropdownOrderStatus.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.lytStatusBtn.background = ContextCompat.getDrawable(it, buttonBkg)
      binding.divider.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.ivDropdown.setImageResource(resId)
      //DrawableCompat.setTint(binding.ivDropdownOrderStatus.drawable, ContextCompat.getColor(it.applicationContext, dropDownArrowColor))
    }
  }

  private fun checkConfirmBtn(order: OrderItem) {
    if (order.isConfirmActionBtn()) {
      binding.lytStatusBtn.visible()
      binding.tvDropdownOrderStatus.setOnClickListener {
        if (order.isConfirmActionBtn()) {
        }
      }
    } else {
      binding.lytStatusBtn.gone()
    }
  }
}