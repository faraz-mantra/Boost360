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

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (cancellingEntity: String,reasonText:String) -> Unit = { _: String, _: String -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_remove_item
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {

  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
     // binding?.buttonDone -> onClicked(cancellingEntity?:"", (binding?.txtReason?.text?.toString()?:""))
    }
  }

}