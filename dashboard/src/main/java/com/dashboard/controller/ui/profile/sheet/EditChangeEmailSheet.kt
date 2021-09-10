package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetChangeEmailBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeEmailSheet : BaseBottomSheetDialog<SheetChangeEmailBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_change_email
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}