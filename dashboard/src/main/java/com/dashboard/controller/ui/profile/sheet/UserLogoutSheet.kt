package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetUserLogoutBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class UserLogoutSheet : BaseBottomSheetDialog<SheetUserLogoutBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_user_logout
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}