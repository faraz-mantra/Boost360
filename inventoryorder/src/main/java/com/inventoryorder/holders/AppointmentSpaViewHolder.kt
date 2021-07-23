package com.inventoryorder.holders

import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_DD_MM_YYYY
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemAppointmentsSpaBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

class AppointmentSpaViewHolder(binding: ItemAppointmentsSpaBinding) : AppBaseRecyclerViewHolder<ItemAppointmentsSpaBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let { setDataResponse(position, it) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(position, data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
    }
  }

  private fun setDataResponse(position: Int, order: OrderItem) {
    val statusValue = OrderStatusValue.fromStatusAppointment(order.status())?.value
    val statusIcon = OrderStatusValue.fromStatusAppointment(order.status())?.icon

    if (statusIcon != null) binding.statusIcon.setImageResource(statusIcon) else binding.statusIcon.gone()

    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      binding.orderType.text = statusValue.plus(order.cancelledText())
    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"

    order.BillingDetails?.let { bill ->
      val currency = takeIf {
        bill.getCurrencyCodeValue().isNullOrEmpty().not()
      }?.let { bill.getCurrencyCodeValue()?.trim() } ?: "INR"
      val formatAmount = "${DecimalFormat("##,##,##0.00").format(BigDecimal(bill.AmountPayableByBuyer!!))}"
      val ss = SpannableString("$formatAmount")
      ss.setSpan(RelativeSizeSpan(0.5f), "$formatAmount".indexOf("."), "$formatAmount".length, 0)
      binding.txtRupees.text = ss
      binding.txtRupeesSymble.text = currency
    }

    binding.txtOrderDate.text = "at ${parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL, timeZone = TimeZone.getTimeZone("IST"))}"
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()

    binding.serviceLocation.icon.setImageResource(R.drawable.ic_service_location)
    binding.serviceLocation.title.text = "${getApplicationContext()?.getString(R.string.service_location)} :"
    binding.serviceLocation.value.text = "Business"
//    binding.serviceLocation.value.text = order.SellerDetails?.Address?.City ?: "NA"

    binding.customer.icon.setImageResource(R.drawable.ic_customer)
    binding.customer.title.text = "${getApplicationContext()?.getString(R.string.customer)} :"
    binding.customer.value.text = order.BuyerDetails?.ContactDetails?.FullName?.capitalizeUtil()

    val itemAptSpa = order.firstItemForAptConsult()
    if (!itemAptSpa?.Product?.ImageUri.isNullOrEmpty()) {
      Picasso.get().load(itemAptSpa?.Product?.ImageUri).into(binding.imageServiceProvider)
    } else {
      binding.imageServiceProvider.visibility = View.GONE
    }

    binding.textWorkType.text = itemAptSpa?.Product?.Name
    activity?.let {
      binding.statusView.background = ContextCompat.getDrawable(it, R.drawable.ic_new_order_bg)
    }    //settings up button
    var colorCode = "#9B9B9B"
    val btnStatusMenu = order.appointmentSpaButtonStatus()
    binding.lytStatusBtn.visible()
    if (btnStatusMenu.isNullOrEmpty().not()) {
      when (val btnOrderMenu = btnStatusMenu.removeFirst()) {
        OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_initiated_order_btn_bkg,
            R.color.white,
            R.drawable.ic_arrow_down_white
          )
        }
        OrderMenuModel.MenuStatus.START_APPOINTMENT -> {
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
        OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_initiated_order_btn_bkg,
            R.color.white,
            R.drawable.ic_arrow_down_white
          )
        }
        OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
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
        OrderMenuModel.MenuStatus.MARK_AS_SERVED -> {
          colorCode = "#78AF00"
          changeButtonStatus(
            btnOrderMenu.title,
            R.drawable.ic_transit_order_btn_green,
            R.color.green_78AF00,
            R.drawable.ic_arrow_down_green
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
      binding.btnAppointmentStatus.setOnClickListener {
        listener?.onItemClick(position, order, RecyclerViewActionType.ORDER_BUTTON_CLICKED.ordinal)
      }
    } else binding.lytStatusBtn.gone()

    if (btnStatusMenu.isNullOrEmpty()) {
      binding.divider.gone()
      binding.ivDropdownAppointment.gone()
    } else {
      binding.ivDropdownAppointment.setOnClickListener {
        listener?.onItemClickView(position, it, order, RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal)
      }
      binding.divider.visible()
      binding.ivDropdownAppointment.visible()
    }

    binding.payment.title.text = getApplicationContext()?.getString(R.string.payment_mode)
    binding.payment.value.text = fromHtml(order.PaymentDetails?.paymentWithColor(colorCode)?.trim() ?: "")

    val staffName = itemAptSpa?.getAptSpaExtraDetail()?.staffName
    binding.txtScheduledDate.text = fromHtml("${itemAptSpa?.getScheduleDateAndTimeSpa()}${if (staffName.isNullOrEmpty()) "" else " by <b><u>$staffName</u></b>"}")

  }

  private fun changeButtonStatus(
    btnTitle: String,
    @DrawableRes buttonBkg: Int,
    @ColorRes dropDownDividerColor: Int,
    @DrawableRes resId: Int
  ) {
    activity?.let {
      binding.btnAppointmentStatus.text = btnTitle
      binding.btnAppointmentStatus.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.lytStatusBtn.background = ContextCompat.getDrawable(it, buttonBkg)
      binding.divider.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.ivDropdownAppointment.setImageResource(resId)
    }
  }

}