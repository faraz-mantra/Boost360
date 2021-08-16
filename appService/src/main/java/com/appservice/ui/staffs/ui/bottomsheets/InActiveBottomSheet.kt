package com.appservice.ui.staffs.ui.bottomsheets

import android.view.KeyEvent
import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomsheetInactiveStaffBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class InActiveBottomSheet :
  BaseBottomSheetDialog<BottomsheetInactiveStaffBinding, BaseViewModel>() {
  private var value: Boolean = true
  var onClicked: (value: Boolean) -> Unit = { }
  var onBackPres: () -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottomsheet_inactive_staff
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnActivateStaff)
    dialog.setCanceledOnTouchOutside(false)
    getDialog()?.setOnKeyListener { _, keyCode, _ ->
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        onBackPres()
        true
      } else false
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnActivateStaff -> {
        dismiss()
        onClicked(value)
      }
    }
  }
}