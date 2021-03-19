package com.inventoryorder.ui.appointmentSpa.sheetAptSpa

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetSendReBookingAptBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class SendReBookingAptSheetDialog : BaseBottomSheetDialog<BottomSheetSendReBookingAptBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_send_re_booking_apt
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)

    binding?.tvSubTitle?.text = "Appointment ID #${orderItem?.ReferenceNumber ?: ""}"

    val number = orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
    val email = orderItem?.BuyerDetails?.ContactDetails?.EmailId
    if (number.isNullOrEmpty().not()) {
      binding?.checkboxSms?.visible()
      binding?.checkboxSms?.text = "via SMS ($number)"
    } else binding?.checkboxSms?.gone()

    if (email.isNullOrEmpty().not()) {
      binding?.checkboxEmail?.visible()
      binding?.checkboxEmail?.text = "via Email ($email)"
    } else binding?.checkboxEmail?.gone()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked()
    }
  }

}