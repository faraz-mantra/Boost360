package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetRequestPaymentOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class RequestPaymentBottomSheetDialog : BaseBottomSheetDialog<BottomSheetRequestPaymentOrderBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_request_payment_order
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonDone -> dismiss()
      binding?.tvCancel -> dismiss()
    }
  }

}