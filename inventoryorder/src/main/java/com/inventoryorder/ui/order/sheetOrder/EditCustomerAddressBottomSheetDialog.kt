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
import com.inventoryorder.model.orderRequest.Address
import com.inventoryorder.model.ordersdetails.OrderItem
import kotlinx.android.synthetic.main.layout_address.*

class EditCustomerAddressBottomSheetDialog(val address: Address) : BaseBottomSheetDialog<BottomSheetEditCustomerAddressBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (address: Address) -> Unit = {address: Address -> }

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
    binding?.layoutAddress?.editAddress?.setText(address?.addressLine)
    binding?.layoutAddress?.editCity?.setText(address?.city)
    binding?.layoutAddress?.editState?.setText(address?.region)
    binding?.layoutAddress?.editPin?.setText(address?.zipcode)
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {

       binding?.buttonDone -> {
         var addr = Address(addressLine = binding?.layoutAddress?.editAddress?.text.toString(),
                 city = binding?.layoutAddress?.editCity?.text.toString(),
                 region = binding?.layoutAddress?.editState?.text.toString(),
                 zipcode = binding?.layoutAddress?.editPin?.text.toString(),)
         onClicked(addr)
       }
    }
  }

}