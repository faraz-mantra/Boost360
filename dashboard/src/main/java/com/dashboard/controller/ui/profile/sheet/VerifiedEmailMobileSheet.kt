package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetVerifiedEmailNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.setIconifiedText

class VerifiedEmailMobileSheet : BaseBottomSheetDialog<SheetVerifiedEmailNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_verified_email_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    binding?.title?.setIconifiedText(
      getString(R.string.hello_worldBlue), R.drawable.ic_check_circle_d,
      "verified", R.color.green_6FCF97,
      "+91 9876543210", R.font.bold
    )
  }
}