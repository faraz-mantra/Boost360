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
import com.inventoryorder.databinding.ItemAppointmentsOrderBinding
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

class AppointmentsViewHolder(binding: ItemAppointmentsOrderBinding) : AppBaseRecyclerViewHolder<ItemAppointmentsOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let { setDataResponse(it) }
    binding.mainView.setOnClickListener { listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal) }
  }

  private fun setDataResponse(order: OrderItem) {
    val statusValue = OrderStatusValue.fromStatusAppointment(order.status())?.value
    val statusIcon = OrderStatusValue.fromStatusAppointment(order.status())?.icon

    if (statusIcon != null) binding.statusIcon.setImageResource(statusIcon) else binding.statusIcon.gone()

    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      binding.orderType.text = statusValue.plus(order.cancelledText())
    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"

    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
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
    binding.serviceLocation.value.text = order.SellerDetails?.Address?.City ?: "NA"

    binding.customer.icon.setImageResource(R.drawable.ic_customer)
    binding.customer.title.text = "${getApplicationContext()?.getString(R.string.customer)} :"
    binding.customer.value.text = order.BuyerDetails?.ContactDetails?.FullName?.capitalizeUtil()

    if (!order.firstItemForConsultation()?.Product?.ImageUri.isNullOrEmpty()) {
      Picasso.get().load(order.firstItemForConsultation()?.Product?.ImageUri).into(binding.imageServiceProvider)
    } else {
      binding.imageServiceProvider.visibility = View.GONE
    }

    binding.textWorkType.text = order.firstItemForConsultation()?.Product?.Name

    //settings up button
    var colorCode = "#9B9B9B"
    val btnStatusMenu = order.appointmentButtonStatus()
    binding.lytStatusBtn.visible()
    if (btnStatusMenu.isNullOrEmpty().not()) {
      when (val btnOrderMenu = btnStatusMenu.removeAt(0)) {
        OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
          colorCode = "#9B9B9B"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_cancelled_order_btn_bkg, R.color.warm_grey_two, R.drawable.ic_arrow_down_grey)
        }
        OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> {
          colorCode = "#FFB900"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_confirmed_order_btn_bkg, R.color.orange, R.drawable.ic_arrow_down_orange)
        }
        OrderMenuModel.MenuStatus.MARK_AS_SERVED -> {
          colorCode = "#52AAC6"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_in_transit_order_btn_bkg, R.color.blue_52AAC6, R.drawable.ic_arrow_down_blue)
        }
        else -> binding.lytStatusBtn.gone()
      }
      binding.btnAppointmentStatus.setOnClickListener {
        listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.ORDER_BUTTON_CLICKED.ordinal)
      }
    } else binding.lytStatusBtn.gone()

    if (btnStatusMenu.isNullOrEmpty()) {
      binding.divider.gone()
      binding.ivDropdownAppointment.gone()
    } else {
      binding.ivDropdownAppointment.setOnClickListener { listener?.onItemClickView(adapterPosition, it, order, RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal) }
      binding.divider.visible()
      binding.ivDropdownAppointment.visible()
    }

    binding.payment.title.text = getApplicationContext()?.getString(R.string.payment_mode)
    binding.payment.value.text = fromHtml(order.PaymentDetails?.paymentWithColor(colorCode)?.trim() ?: "")

    val doctorName = order.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.doctorName
    binding.txtScheduledDate.text = fromHtml("${order.firstItemForConsultation()?.getScheduleDateAndTime()}${if (doctorName.isNullOrEmpty()) "" else " by <b><u>$doctorName</u></b>"}")


    //----------------------------
    val location = order.SellerDetails?.Address?.City?.capitalizeUtil()

    val details = order.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.detailsConsultation()
    val scheduleDate = order.firstItemForConsultation()?.scheduledStartDate()

    val dateApt = parseDate(scheduleDate, FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2)

    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val itemDate = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY) ?: ""
  }

  private fun changeButtonStatus(btnTitle: String, @DrawableRes buttonBkg: Int, @ColorRes dropDownDividerColor: Int, @DrawableRes resId: Int) {
    activity?.let {
      binding.btnAppointmentStatus.text = btnTitle
      binding.btnAppointmentStatus.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.lytStatusBtn.background = ContextCompat.getDrawable(it, buttonBkg)
      binding.divider.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.ivDropdownAppointment.setImageResource(resId)
    }
  }

}