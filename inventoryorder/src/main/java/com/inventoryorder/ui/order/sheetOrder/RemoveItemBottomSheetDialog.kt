package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.*
import com.inventoryorder.model.ordersdetails.OrderItem

class RemoveItemBottomSheetDialog : BaseBottomSheetDialog<BottomSheetRemoveItemBinding, BaseViewModel>() {

  var onClicked: (boolean : Boolean) -> Unit = { boolean : Boolean -> }
  var removeItem = true

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_remove_item
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> {
        onClicked(removeItem)
      }
    }
  }

}