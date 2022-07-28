package com.appservice.ui.address

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetAddAddressBinding
import com.appservice.databinding.BottomSheetWhyAccountBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class AddAddressBottomSheet : BaseBottomSheetDialog<BottomSheetAddAddressBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_add_address
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnAddLocation, binding?.btnEnterManually)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAddLocation -> {}
      binding?.btnEnterManually -> {}
    }
  }
}