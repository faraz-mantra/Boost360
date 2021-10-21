package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetDeliveryTypeBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class DeliveryTypeBottomSheetDialog(val selectedType: String) :
  BaseBottomSheetDialog<BottomSheetDeliveryTypeBinding, BaseViewModel>() {

  var selectedDeliveryType = ""
  var onClicked: (selectedDeliveryType: String) -> Unit = { selectedDeliveryType: String -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_delivery_type
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

    if (selectedType == OrderItem.OrderMode.DELIVERY.name) {
      binding?.radioHome?.isChecked = true
    } else {
      binding?.radioStore?.isChecked = true
    }

    setOnClickListener(binding?.buttonDone, binding?.tvCancel)

    binding?.radioHome?.setOnCheckedChangeListener { p0, isChecked ->
      if (isChecked) {
        selectedDeliveryType = OrderItem.OrderMode.DELIVERY.name
        binding?.radioStore?.isChecked = false
        binding?.radioHome?.isChecked = true
      }
    }

    binding?.radioStore?.setOnCheckedChangeListener { p0, isChecked ->
      if (isChecked) {
        selectedDeliveryType = OrderItem.OrderMode.PICKUP.name
        binding?.radioStore?.isChecked = true
        binding?.radioHome?.isChecked = false
      }
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> onClicked(selectedDeliveryType)
    }
  }

}