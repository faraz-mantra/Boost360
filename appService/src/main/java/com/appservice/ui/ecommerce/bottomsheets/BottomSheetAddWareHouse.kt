package com.appservice.ui.ecommerce.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.RequestAddWareHouseAddress
import com.appservice.databinding.BottomSheetAddWarehouseBinding
import com.appservice.utils.changeColorOfSubstring
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.ValidationUtils

class BottomSheetAddWareHouse : BaseBottomSheetDialog<BottomSheetAddWarehouseBinding, BaseViewModel>() {

  var onClicked: (value: RequestAddWareHouseAddress) -> Unit = { }
  var requestAddWareHouseAddress: RequestAddWareHouseAddress? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_add_warehouse
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setupUIColor()
    setOnClickListener(binding?.btnCancel, binding?.btnSaveChanges)
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.warehouse_name, R.color.colorAccent, "*", binding?.ctvWarehouseName!!)
    changeColorOfSubstring(R.string.warehouse_address, R.color.colorAccent, "*", binding?.ctvWarehouseAddress!!)
    changeColorOfSubstring(R.string.contact_number, R.color.colorAccent, "*", binding?.ctvContactNumber!!)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSaveChanges -> {
        if (isValid()) {
          onClicked(requestAddWareHouseAddress!!)
          dismiss()
        }
      }
      binding?.btnCancel -> {
        dismiss()
      }
    }
  }

  private fun isValid(): Boolean {
    val phoneNumber = binding?.petContactNo?.text
    val address = binding?.cetWarehouseAddress?.text
    val name = binding?.cetWarehouseNameHint?.text
    when {
      name.isNullOrEmpty() -> {
        showShortToast(getString(R.string.warehouse_name_cannot_empty))
        return false
      }
      address.isNullOrEmpty() -> {
        showShortToast(getString(R.string.warehouse_address_cannot_empty))
        return false

      }
      !ValidationUtils.isMobileNumberValid(phoneNumber.toString()) -> {
        showShortToast(getString(R.string.invalid_phone_number))
        return false
      }
      requestAddWareHouseAddress == null -> requestAddWareHouseAddress = RequestAddWareHouseAddress()
    }
    requestAddWareHouseAddress?.contactNumber = phoneNumber.toString()
    requestAddWareHouseAddress?.fullAddress = address.toString()
    requestAddWareHouseAddress?.name = name.toString()
    return true

  }
}