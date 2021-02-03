package com.inventoryorder.holders

import android.graphics.Paint
import android.util.Log
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
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemAppointmentsOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.util.*

class AppointmentsViewHolder(binding: ItemAppointmentsOrderBinding) : AppBaseRecyclerViewHolder<ItemAppointmentsOrderBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? OrderItem
    data?.let {
      /*binding.aptDate.title.text = activity?.resources?.getString(R.string.date_apt)
      binding.createDate.title.text = activity?.resources?.getString(R.string.date_order)
      binding.payment.title.text = activity?.resources?.getString(R.string.payment)
      binding.location.title.text = activity?.resources?.getString(R.string.location)*/
      setDataResponse(it)
    }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
    }
  }

  private fun setDataResponse(order: OrderItem) {
    val statusValue = OrderStatusValue.fromStatusAppointment(order.status())?.value

    if (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name == order.status().toUpperCase(Locale.ROOT)) {
      if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
        binding.orderType.text = OrderStatusValue.ESCALATED_2.value
      } else binding.orderType.text = statusValue.plus(order.cancelledText())
    } else binding.orderType.text = statusValue

    binding.orderId.text = "# ${order.ReferenceNumber}"

    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "₹"
      binding.txtRupees.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding.txtOrderDate.text = "at ${parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL, timeZone = TimeZone.getTimeZone("IST"))}"
    binding.payment.value.text = order.PaymentDetails?.payment()?.trim()

    binding.payment.title.text = getApplicationContext()?.getString(R.string.payment_mode)
    binding.payment.value.text = "${order.PaymentDetails?.Method},"
    binding.payment.status.visibility = View.VISIBLE
    binding.payment.status.text = order.PaymentDetails?.Status

    binding.serviceLocation.icon.setImageResource(R.drawable.ic_service_location)
    binding.serviceLocation.title.text = "${getApplicationContext()?.getString(R.string.service_location)} :"
    binding.serviceLocation.value.text = order.SellerDetails?.Address?.City ?: "NA"

    binding.customer.icon.setImageResource(R.drawable.ic_customer)
    binding.customer.title.text = "${getApplicationContext()?.getString(R.string.customer)} :"
    binding.customer.value.text = order.BuyerDetails?.ContactDetails?.FullName

    if (!order.firstItemForConsultation()?.Product?.ImageUri.isNullOrEmpty()) {
      Picasso.get().load(order.firstItemForConsultation()?.Product?.ImageUri).into(binding.imageServiceProvider)
    } else {
      binding.imageServiceProvider.visibility = View.GONE
    }

    binding.textWorkType.text = order.firstItemForConsultation()?.Product?.Description



    val location = order.SellerDetails?.Address?.City?.capitalizeUtil()
 /*   if (location.isNullOrEmpty().not()) {
      binding.location.mainView.visible()
      binding.location.value.text = location
    } else binding.location.mainView.gone()*/

    val sizeItem = order.Items?.size ?: 0
   // binding.itemCount.text = takeIf { sizeItem > 1 }?.let { "Services" } ?: "Service"
//    binding.itemDesc.text = order.getTitlesBooking()
    val details = order.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.detailsConsultation()
    val scheduleDate = order.firstItemForConsultation()?.scheduledStartDate()

    //binding.itemDesc.text = details

    val dateApt = parseDate(scheduleDate, FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2)
/*    if (dateApt.isNullOrEmpty().not()) {
      binding.aptDate.mainView.visible()
      binding.aptDate.value.text = dateApt
    } else binding.aptDate.mainView.gone()*/

    /*binding.itemMore.visibility = takeIf { (sizeItem > 1) }?.let {
      binding.itemMore.text = "+${sizeItem - 1} more"
      View.VISIBLE
    } ?: View.GONE*/

    val todayDate = getCurrentDate().parseDate(FORMAT_DD_MM_YYYY) ?: ""
    val itemDate = parseDate(order.CreatedOn, FORMAT_SERVER_DATE, FORMAT_DD_MM_YYYY) ?: ""

    //settings up button
    var colorCode = "#4a4a4a"
    val btnStatusMenu = order.orderBtnStatus()
    binding.lytStatusBtn.visible()
    if (btnStatusMenu.isNullOrEmpty().not()) {
      when (val btnOrderMenu = btnStatusMenu.removeAt(0)) {
        OrderMenuModel.MenuStatus.CONFIRM_ORDER -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.CANCEL_ORDER -> {
          colorCode = "#9B9B9B"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_cancelled_order_btn_bkg, R.color.warm_grey_two, R.drawable.ic_arrow_down_grey)
        }
        OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> {
          colorCode = "#FFB900"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_confirmed_order_btn_bkg, R.color.orange, R.drawable.ic_arrow_down_orange)
        }
        OrderMenuModel.MenuStatus.MARK_AS_DELIVERED -> {
          colorCode = "#52AAC6"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_in_transit_order_btn_bkg, R.color.blue_52AAC6, R.drawable.ic_arrow_down_blue)
        }
        OrderMenuModel.MenuStatus.MARK_AS_SHIPPED -> {
          colorCode = "#52AAC6"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_in_transit_order_btn_bkg, R.color.blue_52AAC6, R.drawable.ic_arrow_down_blue)
        }
        else -> binding.lytStatusBtn.gone()
      }
      binding.tvDropdownOrderStatus.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.APPOINTMENT_DROPDOWN_CLICKED.ordinal) }
    } else binding.lytStatusBtn.gone()

    if (btnStatusMenu.isNullOrEmpty()) {
      binding.divider.gone()
      binding.ivDropdownAppointment.gone()
    } else {
      binding.ivDropdownAppointment.setOnClickListener { listener?.onItemClickView(adapterPosition, it, order, RecyclerViewActionType.BUTTON_ACTION_ITEM.ordinal) }
      binding.divider.visible()
      binding.ivDropdownAppointment.visible()
    }

    OrderSummaryModel.OrderStatus.from(order.status())?.let {
      when (it) {
        OrderSummaryModel.OrderStatus.ORDER_CANCELLED -> {
          changeBackground(View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey, R.color.primary_grey)
         // binding.btnConfirm.gone()
        }
        else -> {
          changeBackground(View.VISIBLE, View.GONE, R.drawable.ic_apt_order_bg, R.color.watermelon_light, R.color.light_green)
          checkConfirmBtn(order)
        }
      }
    }

    binding.itemMore.text = "by ${order?.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.staffName}"
    binding.itemMore.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG).let { binding.itemMore.paintFlags = it }
    binding.itemMore.visibility = if (!order?.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.staffName.isNullOrBlank()) View.VISIBLE else View.GONE
  }

  private fun changeButtonStatus(btnTitle: String, @DrawableRes buttonBkg: Int, @ColorRes dropDownDividerColor: Int, @DrawableRes resId: Int) {
    activity?.let {
      binding.tvDropdownOrderStatus.text = btnTitle
      binding.tvDropdownOrderStatus.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.lytStatusBtn.background = ContextCompat.getDrawable(it, buttonBkg)
      binding.divider.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding.ivDropdownAppointment.setImageResource(resId)
      //DrawableCompat.setTint(binding.ivDropdownOrderStatus.drawable, ContextCompat.getColor(it.applicationContext, dropDownArrowColor))
    }
  }

  private fun checkConfirmBtn(order: OrderItem) {
   /* if (order.isConfirmActionBtn()) {
      binding.btnConfirm.visible()
      binding.btnConfirm.setOnClickListener { listener?.onItemClick(adapterPosition, order, RecyclerViewActionType.BOOKING_CONFIRM_CLICKED.ordinal) }
    } else binding.btnConfirm.gone()*/
  }

  private fun buttonDisable(color: Int, border: Int) {
   /* activity?.let {
      binding.btnConfirm.setTextColor(ContextCompat.getColor(it, color))
      binding.btnConfirm.background = ContextCompat.getDrawable(it, border)
    }*/
  }

  private fun backgroundGrey(confirm: Int, detail: Int, btn: Int, orderBg: Int, primaryGrey: Int) {
  /*  binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    setColorView(primaryGrey, binding.aptDate.title, binding.payment.title, binding.location.title, binding.bookingId,
        binding.txtRupees, binding.aptDate.value, binding.payment.value, binding.location.value, binding.itemCount,
        binding.itemDesc, binding.itemMore)
    activity?.let {
      binding.btnConfirm.setTextColor(ContextCompat.getColor(it, primaryGrey))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.btnConfirm.background = ContextCompat.getDrawable(it, R.drawable.btn_rounded_grey_border)
    }
    binding.btnConfirm.paintFlags = binding.btnConfirm.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG*/
  }

  private fun setColorView(color: Int, vararg view: CustomTextView) {
    activity?.let { ac -> view.forEach { it.setTextColor(ContextCompat.getColor(ac, color)) } }
  }

  private fun changeBackground(detail: Int, btn: Int, orderBg: Int, rupeesColor: Int, dateColor: Int) {
  /*  binding.detailsOrder.visibility = detail
    binding.next2.visibility = btn
    activity?.let {
      binding.aptDate.value.setTextColor(ContextCompat.getColor(it, dateColor))
      binding.orderType.background = ContextCompat.getDrawable(it, orderBg)
      binding.txtRupees.setTextColor(ContextCompat.getColor(it, rupeesColor))
    }*/
  }

}