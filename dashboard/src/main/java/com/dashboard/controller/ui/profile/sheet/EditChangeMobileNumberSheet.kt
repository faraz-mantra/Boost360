package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetChangeMobileNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeMobileNumberSheet : BaseBottomSheetDialog<SheetChangeMobileNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_change_mobile_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}