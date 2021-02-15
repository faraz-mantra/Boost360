package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetCancelOrderBinding
import com.inventoryorder.databinding.BottomSheetDeliveryTypeBinding
import com.inventoryorder.databinding.BottomSheetEditCustomerAddressBinding
import com.inventoryorder.databinding.BottomSheetEditCustomerInfoBinding
import com.inventoryorder.model.ordersdetails.OrderItem

class EditCustomerAddressBottomSheetDialog : BaseBottomSheetDialog<BottomSheetEditCustomerAddressBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (cancellingEntity: String,reasonText:String) -> Unit = { _: String, _: String -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_edit_customer_address
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    binding?.checkboxAddressSame?.setOnCheckedChangeListener { p0, isChecked ->
      binding?.lytShippingAddress?.visibility = if (isChecked) View.GONE else View.VISIBLE
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
     // binding?.buttonDone -> onClicked(cancellingEntity?:"", (binding?.txtReason?.text?.toString()?:""))
    }
  }

}