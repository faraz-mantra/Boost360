package com.inventoryorder.ui.appointmentSpa.sheetAptSpa

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetMarkPaymentDoneBinding
import com.inventoryorder.model.orderRequest.paymentRequest.PaymentReceivedRequest
import com.inventoryorder.model.ordersdetails.OrderItem

class MarkPaymentDoneAptSheetDialog : BaseBottomSheetDialog<BottomSheetMarkPaymentDoneBinding, BaseViewModel>() {

  private var paymentProvider: String? = PaymentReceivedRequest.PaymentProvider.cash.name
  private var orderItem: OrderItem? = null
  var onClicked: (request: PaymentReceivedRequest) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_mark_payment_done
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
    binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
      val radioButton: View = group.findViewById(checkedId)
      paymentProvider = when (radioButton) {
        binding?.radioCash -> PaymentReceivedRequest.PaymentProvider.cash.name
        binding?.radioCard -> PaymentReceivedRequest.PaymentProvider.card.name
        binding?.radioUpi -> PaymentReceivedRequest.PaymentProvider.upi.name
        binding?.radioOthers -> PaymentReceivedRequest.PaymentProvider.others.name
        else -> PaymentReceivedRequest.PaymentProvider.cash.name
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> {
        onClicked(
          PaymentReceivedRequest(
            paymentProvider,
            "",
            orderItem?.PaymentDetails?.method(),
            orderItem?._id,
            orderItem?.PaymentDetails?.TransactionId ?: ""
          )
        )
      }
    }
  }

}