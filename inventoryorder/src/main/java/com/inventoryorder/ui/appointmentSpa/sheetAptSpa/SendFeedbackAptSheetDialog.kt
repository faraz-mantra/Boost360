package com.inventoryorder.ui.appointmentSpa.sheetAptSpa

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetSendFeedbackAptBinding
import com.inventoryorder.model.orderRequest.feedback.FeedbackRequest
import com.inventoryorder.model.ordersdetails.OrderItem

class SendFeedbackAptSheetDialog :
  BaseBottomSheetDialog<BottomSheetSendFeedbackAptBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: (request: FeedbackRequest) -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_send_feedback_apt
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
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked(
        FeedbackRequest(
          orderItem?._id,
          binding?.txtReason?.text?.toString() ?: ""
        )
      )
    }
  }

}