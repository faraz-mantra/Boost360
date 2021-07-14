package com.appservice.ui.staffs.ui.bottomsheets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomsheetInactiveStaffConfirmationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class InActiveStaffConfirmationBottomSheet :
  BaseBottomSheetDialog<BottomsheetInactiveStaffConfirmationBinding, BaseViewModel>() {
  private var value: Boolean = true
  var onClicked: (value: Boolean) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottomsheet_inactive_staff_confirmation
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnDone, binding?.btnCancel)

  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        onClicked(value)
        dismiss()
      }
      binding?.btnCancel -> {
        dismiss()
      }
    }
  }


}