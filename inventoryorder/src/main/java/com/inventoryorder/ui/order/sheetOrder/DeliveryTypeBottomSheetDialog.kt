package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetCancelOrderBinding
import com.inventoryorder.databinding.BottomSheetDeliveryTypeBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class DeliveryTypeBottomSheetDialog : BaseBottomSheetDialog<BottomSheetDeliveryTypeBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (cancellingEntity: String,reasonText:String) -> Unit = { _: String, _: String -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_delivery_type
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
   /* setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.tvSubTitle?.text = "Order ID #${orderItem?.ReferenceNumber ?: ""}"
    binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
      val radioButton: View = group.findViewById(checkedId)
      cancellingEntity= when (radioButton){
        binding?.radioCustomer -> OrderItem.CancellingEntity.BUYER.name
        else -> OrderItem.CancellingEntity.SELLER.name
      }
    }*/
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
     // binding?.buttonDone -> onClicked(cancellingEntity?:"", (binding?.txtReason?.text?.toString()?:""))
    }
  }

}