package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetChangeUsernameBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeUserNameSheet : BaseBottomSheetDialog<SheetChangeUsernameBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_change_username
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}