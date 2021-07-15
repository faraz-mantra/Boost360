package com.inventoryorder.ui.order.createorder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetEditCustomerAddressBinding
import com.inventoryorder.model.orderRequest.Address
import com.inventoryorder.model.ordersdetails.OrderItem
import com.onboarding.nowfloats.model.CityDataModel
import com.onboarding.nowfloats.ui.CitySearchDialog

class EditCustomerAddressBottomSheetDialog(val address: Address) : BaseBottomSheetDialog<BottomSheetEditCustomerAddressBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (address: Address) -> Unit = { }

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
    binding?.layoutAddress?.editAddress?.setText(address.addressLine ?: "")
    binding?.layoutAddress?.editCity?.setText(address.city ?: "")
    binding?.layoutAddress?.editState?.setText(address.region ?: "")
    binding?.layoutAddress?.editPin?.setText(address.zipcode ?: "")
    setOnClickListener(binding?.buttonDone, binding?.tvCancel, binding?.layoutAddress?.editCity)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonDone -> {
        val addressN = Address(
          addressLine = binding?.layoutAddress?.editAddress?.text.toString(),
          city = binding?.layoutAddress?.editCity?.text.toString(),
          region = binding?.layoutAddress?.editState?.text.toString(),
          zipcode = binding?.layoutAddress?.editPin?.text.toString(),
        )
        onClicked(addressN)
        dismiss()
      }
      binding?.layoutAddress?.editCity -> {
        val dialog = CitySearchDialog()
        dialog.onClicked = { setCityState(it) }
        dialog.show(parentFragmentManager, dialog.javaClass.name)
      }
      binding?.tvCancel -> dismiss()
    }
  }

  private fun setCityState(cityDataModel: CityDataModel) {
    binding?.layoutAddress?.editCity?.setText(cityDataModel.getCityName())
    binding?.layoutAddress?.editState?.setText(cityDataModel.getStateName())
  }
}