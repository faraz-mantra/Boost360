package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetDeliveredOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class DeliveredBottomSheetDialog : BaseBottomSheetDialog<BottomSheetDeliveredOrderBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: (message: String) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_delivered_order
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.tvSubTitle?.text = "Order ID #${orderItem?.ReferenceNumber ?: ""}"
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked(binding?.tvDesc?.text?.toString() ?: "")
    }
  }
}