package com.dashboard.controller.ui.profile.sheet

import com.dashboard.R
import com.dashboard.databinding.SheetVerifyOtpEmailNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class VerifyOtpEmailMobileSheet : BaseBottomSheetDialog<SheetVerifyOtpEmailNumberBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_verify_otp_email_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}