package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.*
import com.inventoryorder.model.ordersdetails.OrderItem

class AddDeliveryFeeBottomSheetDialog(val deliveryFee : Double = 0.0) : BaseBottomSheetDialog<BottomSheetAddDeliveryFeeBinding, BaseViewModel>() {

  var onClicked: (deliveryFeeValue: Double) -> Unit = { value : Double -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_add_delivery_fee
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  override fun onCreateView() {
    if (deliveryFee > 0) binding?.editDeliveryFee?.setText(deliveryFee.toString())
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {

      binding?.buttonDone ->  {

        if (binding?.editDeliveryFee?.text?.isNullOrEmpty()?.not() == true) {
          onClicked(binding?.editDeliveryFee?.text?.toString()?.toDouble() ?: 0.0)
        } else {
          onClicked(0.0)
        }
      }

      binding?.tvCancel -> onClicked(-1.0)
    }
  }
}