package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetDeliveredOrderBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class DeliveredBottomSheetDialog :
  BaseBottomSheetDialog<BottomSheetDeliveredOrderBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  private var feedback: Boolean = true
  var onClicked: (message: String, feedback: Boolean) -> Unit = { s: String, b: Boolean -> }

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
    binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
      val radioButton: View = group.findViewById(checkedId)
      feedback = when (radioButton) {
        binding?.radioFeedback -> {
          binding?.messageView?.visible()
          true
        }
        binding?.radioNoFeedback -> {
          binding?.messageView?.gone()
          false
        }
        else -> {
          binding?.messageView?.visible()
          true
        }
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked(binding?.tvDesc?.text?.toString() ?: "", feedback)
    }
  }
}